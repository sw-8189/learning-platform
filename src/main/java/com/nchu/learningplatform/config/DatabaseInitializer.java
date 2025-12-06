package com.nchu.learningplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;

/**
 * 数据库初始化器
 * 在应用启动时自动创建数据库和表结构
 */
@Component
@Order(1) // 确保在其他组件之前执行
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Override
    public void run(String... args) {
        try {
            // 加载驱动
            Class.forName(driverClassName);

            // 从 JDBC URL 中提取数据库名
            // jdbc:mysql://localhost:3306/learning_platform?useSSL=false&serverTimezone=Asia/Shanghai
            // 注意：URL中可能包含"Asia/Shanghai"，所以不能简单用split("/")
            String dbName = "learning_platform";
            try {
                // 找到最后一个"/"的位置（在端口号之后）
                // jdbc:mysql://localhost:3306/learning_platform?...
                int protocolEnd = jdbcUrl.indexOf("://");
                if (protocolEnd > 0) {
                    String afterProtocol = jdbcUrl.substring(protocolEnd + 3);
                    // 找到第一个"/"（在主机:端口之后）
                    int firstSlash = afterProtocol.indexOf("/");
                    if (firstSlash > 0) {
                        String dbPart = afterProtocol.substring(firstSlash + 1);
                        // 移除查询参数（找到第一个"?"）
                        int questionMark = dbPart.indexOf("?");
                        if (questionMark > 0) {
                            dbName = dbPart.substring(0, questionMark);
                        } else {
                            dbName = dbPart;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("解析数据库名失败，使用默认值: " + dbName);
            }

            // 连接到 MySQL 服务器（不指定数据库）
            // jdbc:mysql://localhost:3306/learning_platform?...
            // 需要提取为: jdbc:mysql://localhost:3306
            String serverUrl = jdbcUrl;
            int lastSlash = serverUrl.lastIndexOf("/");
            if (lastSlash > 0) {
                serverUrl = serverUrl.substring(0, lastSlash);
            }
            // 移除参数
            if (serverUrl.contains("?")) {
                serverUrl = serverUrl.substring(0, serverUrl.indexOf("?"));
            }

            System.out.println("正在连接到 MySQL 服务器: " + serverUrl);
            System.out.println("准备创建数据库: " + dbName);

            try (Connection conn = DriverManager.getConnection(serverUrl, username, password);
                 Statement stmt = conn.createStatement()) {

                // 创建数据库（如果不存在）
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName +
                        " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                System.out.println("✓ 数据库 " + dbName + " 创建成功或已存在");

                // 切换到目标数据库
                stmt.executeUpdate("USE " + dbName);

                // 读取 SQL 脚本
                ClassPathResource resource = new ClassPathResource("sql/schema-learning-platform.sql");
                String sqlScript = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                // 改进的SQL解析：按分号分割，但保留多行语句
                // 先移除注释行
                String[] lines = sqlScript.split("\n");
                StringBuilder cleanedScript = new StringBuilder();
                boolean inCreateDatabase = false;
                for (String line : lines) {
                    String trimmed = line.trim();
                    // 跳过空行和注释行
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    // 跳过 CREATE DATABASE 和 USE 语句（已经处理过了）
                    if (trimmed.toUpperCase().startsWith("CREATE DATABASE")) {
                        inCreateDatabase = true;
                        continue;
                    }
                    // 如果正在处理CREATE DATABASE语句，跳过直到遇到分号
                    if (inCreateDatabase) {
                        if (trimmed.endsWith(";")) {
                            inCreateDatabase = false;
                        }
                        continue;
                    }
                    if (trimmed.toUpperCase().startsWith("USE ")) {
                        continue;
                    }
                    cleanedScript.append(line).append("\n");
                }
                
                // 按分号分割SQL语句（但要注意字符串中的分号）
                String cleaned = cleanedScript.toString();
                String[] statements = cleaned.split(";");
                
                for (String statement : statements) {
                    String sql = statement.trim();
                    if (sql.isEmpty()) {
                        continue;
                    }
                    
                    // 移除末尾的分号（如果有）
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1).trim();
                    }
                    
                    if (!sql.isEmpty()) {
                        try {
                            System.out.println("执行SQL: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                            stmt.executeUpdate(sql);
                            System.out.println("✓ SQL执行成功");
                        } catch (Exception e) {
                            // 忽略已存在的表或重复数据错误
                            String errorMsg = e.getMessage();
                            if (errorMsg != null) {
                                String lowerMsg = errorMsg.toLowerCase();
                                if (lowerMsg.contains("already exists") || 
                                    lowerMsg.contains("duplicate entry") ||
                                    (lowerMsg.contains("table") && lowerMsg.contains("doesn't exist")) ||
                                    lowerMsg.contains("table") && lowerMsg.contains("already exists")) {
                                    // 静默忽略这些错误
                                    System.out.println("⚠ 忽略已知错误: " + errorMsg);
                                    continue;
                                }
                            }
                            // 打印其他错误
                            System.err.println("❌ 执行 SQL 时出错: " + sql.substring(0, Math.min(100, sql.length())));
                            System.err.println("   错误信息: " + errorMsg);
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("✓ 数据库表结构和初始数据初始化完成");
            }
        } catch (Exception e) {
            System.err.println("⚠ 数据库初始化失败: " + e.getMessage());
            System.err.println("   如果数据库已存在，可以忽略此错误");
            // 不抛出异常，允许应用继续启动（可能数据库已经存在）
        }
    }
}

