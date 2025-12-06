package com.nchu.learningplatform.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MyBatisConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-dir:uploads/avatar}")
    private String avatarUploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(avatarUploadDir);
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir")).resolve(avatarUploadDir);
            }
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException("无法创建头像上传目录: " + avatarUploadDir, e);
        }
    }

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // 映射本地上传目录为 /uploads/** 静态资源（包含头像和课程封面等）
        // 获取uploads目录的父目录路径，以支持所有上传文件
        java.nio.file.Path uploadsDir = java.nio.file.Paths.get(System.getProperty("user.dir")).resolve("uploads");
        String uploadsLocation = "file:" + uploadsDir.toAbsolutePath().toString().replace("\\", "/") + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsLocation);

        // 保留默认 static 目录映射（放在最后，避免拦截 API 路径）
        // Spring Boot 会先匹配 Controller 路径，再匹配静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}