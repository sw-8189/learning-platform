# 智慧学习平台

## 项目简介

智慧学习平台是一个基于Spring Boot和Vue.js的在线学习管理系统，提供课程管理、社区交流、学习路径规划等功能。

## 技术栈

- **后端**: Spring Boot 3.x, MyBatis, MySQL
- **前端**: Vue.js 2.x, Axios, ECharts
- **构建工具**: Maven

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 数据库配置

创建数据库并导入SQL脚本：

```sql
CREATE DATABASE learning_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入 `src/main/resources/sql/schema-learning-platform.sql` 文件。

### 2. 环境配置

项目支持通过环境变量或配置文件进行配置：

#### 方式一：使用环境变量（推荐）

设置以下环境变量：

```bash
# 数据库配置
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=learning_platform
export DB_USERNAME=root
export DB_PASSWORD=your_password

# 邮件配置
export MAIL_USER=your_email@example.com
export MAIL_PASSWORD=your_email_password

# 管理员邀请码
export ADMIN_INVITE_CODE=your_admin_code

# 服务器端口（可选）
export SERVER_PORT=8080
```

#### 方式二：修改配置文件

编辑 `src/main/resources/application.yml`，修改数据库和邮件配置。

### 3. 运行项目

```bash
# 使用Maven运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/learning-platform-*.jar
```

### 4. 访问应用

- 前端首页: http://localhost:8080/index.html
- 登录页面: http://localhost:8080/login.html
- API接口: http://localhost:8080/api/**

## 项目结构

```
learning-platform/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nchu/learningplatform/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── service/         # 服务层
│   │   │       ├── mapper/          # MyBatis映射器
│   │   │       ├── entity/          # 实体类
│   │   │       └── util/            # 工具类
│   │   └── resources/
│   │       ├── static/              # 静态资源
│   │       │   ├── css/             # 样式文件
│   │       │   ├── js/              # JavaScript文件
│   │       │   └── image/           # 图片资源
│   │       ├── mapper/              # MyBatis XML映射文件
│   │       └── application.yml      # 应用配置
│   └── test/                         # 测试代码
└── uploads/                          # 文件上传目录（运行时生成）
```

## 配置说明

### 数据库配置

在 `application.yml` 中配置数据库连接信息，或使用环境变量：

- `DB_HOST`: 数据库主机地址（默认: localhost）
- `DB_PORT`: 数据库端口（默认: 3306）
- `DB_NAME`: 数据库名称（默认: learning_platform）
- `DB_USERNAME`: 数据库用户名（默认: root）
- `DB_PASSWORD`: 数据库密码

### 文件上传配置

文件上传目录配置（支持环境变量）：

- `AVATAR_UPLOAD_DIR`: 头像上传目录（默认: uploads/avatar）
- `COURSE_UPLOAD_DIR`: 课程封面上传目录（默认: uploads/course）
- `COMMUNITY_UPLOAD_DIR`: 社区附件上传目录（默认: uploads/community）

### 邮件配置

邮件服务配置（支持环境变量）：

- `MAIL_USER`: 发件人邮箱地址
- `MAIL_PASSWORD`: 发件人邮箱授权码

## API接口

项目采用RESTful API设计，主要接口包括：

- `/api/auth/*` - 认证相关接口
- `/api/users/*` - 用户相关接口
- `/api/courses/*` - 课程相关接口
- `/api/community/*` - 社区相关接口
- `/api/messages/*` - 消息相关接口
- `/api/admin/*` - 管理员接口

详细API文档请参考代码中的Controller类。

## 开发说明

### 前后端分离

项目采用前后端分离架构：

- **后端**: Spring Boot提供RESTful API
- **前端**: 静态HTML + Vue.js + Axios

### 代码规范

- 所有内联CSS和JavaScript已提取到独立文件
- 路径配置统一使用相对路径或环境变量
- 配置文件支持环境变量覆盖

## 常见问题

### 1. 数据库连接失败

检查数据库配置是否正确，确保MySQL服务已启动。

### 2. 文件上传失败

检查 `uploads` 目录是否存在且有写入权限。

### 3. 邮件发送失败

检查邮件配置是否正确，特别是授权码是否正确。

## 许可证

本项目仅供学习使用。

