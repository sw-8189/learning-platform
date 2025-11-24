-- learning-platform/src/main/resources/sql/schema-learning-platform.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS learning_platform
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE learning_platform;

-- 用户表（包含你的学习信息字段）
DROP TABLE IF EXISTS user;
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      phone VARCHAR(20) UNIQUE,
                      email VARCHAR(100) UNIQUE,
                      avatar_url VARCHAR(2000) DEFAULT '/image/avatar/default-avatar.png',
                      role VARCHAR(20) NOT NULL DEFAULT 'USER',
                      gender VARCHAR(20),
                      learning_preference VARCHAR(255),
                      course_interest VARCHAR(255),
                      learning_goal VARCHAR(50),
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                      update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- 课程表
DROP TABLE IF EXISTS course;
CREATE TABLE course (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(100) NOT NULL,
                        description TEXT,
                        level VARCHAR(20),
                        category VARCHAR(50),
                        cover_url VARCHAR(255),
                        teacher VARCHAR(50),
                        price DECIMAL(10,2) DEFAULT 0,
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户课程关联表
DROP TABLE IF EXISTS user_course;
CREATE TABLE user_course (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE KEY uk_user_course (user_id, course_id),
                             KEY idx_user_id (user_id),
                             KEY idx_course_id (course_id)
);

-- 插入示例课程数据
INSERT INTO course (title, description, level, category, cover_url, teacher, price) VALUES
                                                                                        ('Java 基础入门', '从零开始学习 Java 编程语言，掌握面向对象编程基础', '初级', '编程开发', '/image/course/java-basic.jpg', '张老师', 0),
                                                                                        ('前端开发实战', '学习 HTML、CSS、JavaScript，构建现代化网页应用', '初级', '前端开发', '/image/course/frontend.jpg', '李老师', 0),
                                                                                        ('Python 数据分析', '使用 Python 进行数据清洗、处理和可视化分析', '中级', '数据科学', '/image/course/python-data.jpg', '王老师', 199.00),
                                                                                        ('Spring Boot 企业级开发', '掌握 Spring Boot 框架，构建企业级应用', '中级', '后端开发', '/image/course/spring-boot.jpg', '赵老师', 299.00),
                                                                                        ('Vue.js 全栈开发', '从前端到后端，使用 Vue.js 构建全栈应用', '中级', '前端开发', '/image/course/vue-fullstack.jpg', '陈老师', 399.00),
                                                                                        ('机器学习实战', '学习机器学习算法，使用 Python 实现实际案例', '高级', '人工智能', '/image/course/machine-learning.jpg', '刘老师', 499.00),
                                                                                        ('UI/UX 设计基础', '掌握用户界面和用户体验设计原则', '初级', '设计创意', '/image/course/ui-ux.jpg', '孙老师', 159.00),
                                                                                        ('产品经理实战', '学习产品规划、需求分析和项目管理', '中级', '商业管理', '/image/course/product-manager.jpg', '周老师', 259.00);

