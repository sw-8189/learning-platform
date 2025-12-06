// learning-platform/src/main/java/com/nchu/learningplatform/controller/UserController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

/**
 * 用户相关接口
 * 提示：这里主要提供一个 /api/users/me，方便前端获取当前登录用户信息
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AuthController authController;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 通过请求头里的 token 获取当前用户信息
     * 前端调用示例：
     *   axios.get('/api/users/me', { headers: { Authorization: localStorage.getItem('token') } })
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody User user) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }

        user.setId(userId);

        // 后端防护：不要把 base64 数据或过长字符串写入 avatar_url
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null) {
            if (avatarUrl.startsWith("data:")) {
                return ResponseEntity.badRequest().body(Map.of("message", "头像不应为 base64 数据，请使用文件上传接口上传图片"));
            }
            // 与数据库定义保持一致（schema 中为 VARCHAR(2000)），如果项目中列较短，限制更小
            if (avatarUrl.length() > 2000) {
                return ResponseEntity.badRequest().body(Map.of("message", "avatarUrl 长度超限"));
            }
        }

        try {
            userService.updateUserInfo(user);
            return ResponseEntity.ok(Map.of("message", "信息更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "信息更新失败: " + e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "参数不完整"));
        }

        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 获取用户课程
     */
    @GetMapping("/me/courses")
    public ResponseEntity<?> getUserCourses(@RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }

        try {
            // 获取用户选课记录
            List<UserCourse> userCourses = userCourseMapper.findByUserId(userId);

            // 获取课程详情
            List<Course> courses = new ArrayList<>();
            for (UserCourse userCourse : userCourses) {
                Course course = courseMapper.findById(userCourse.getCourseId());
                if (course != null) {
                    courses.add(course);
                }
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "获取课程失败: " + e.getMessage()));
        }


    }

    // 语言: java
    // 将下面方法与字段插入到 UserController 类中（保持类其余部分不变）

    @Value("${app.upload.avatar-dir:uploads/avatar}")
    private String avatarUploadDir;

    // 在类中保留原 @Value 注入（如果已有可以复用），这里只展示 uploadAvatar 方法主体替换
    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("avatar") MultipartFile avatar) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "未上传文件"));
        }

        try {
            // 解析 avatarUploadDir 为运行目录下的绝对路径，保证与 MyBatisConfig 一致
            java.nio.file.Path dir = java.nio.file.Paths.get(avatarUploadDir);
            if (!dir.isAbsolute()) {
                dir = java.nio.file.Paths.get(System.getProperty("user.dir")).resolve(avatarUploadDir);
            }
            java.nio.file.Files.createDirectories(dir);

            String original = avatar.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = java.util.UUID.randomUUID().toString() + ext;
            java.nio.file.Path target = dir.resolve(filename);

            // 保存文件
            java.io.File targetFile = target.toFile();
            if (targetFile == null) {
                throw new RuntimeException("无法创建目标文件");
            }
            avatar.transferTo(targetFile);

            // 注意：文件保存在 uploads/avatar/ 目录，静态资源映射 /uploads/** 到该目录
            // 所以访问路径应该是 /uploads/avatar/filename
            String avatarUrl = "/uploads/avatar/" + filename;

            // 更新用户 avatarUrl
            User u = new User();
            u.setId(userId);
            u.setAvatarUrl(avatarUrl);
            userService.updateUserInfo(u);

            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "上传失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索用户（用于私信功能）
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String keyword) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }

        try {
            // 使用UserMapper的搜索功能，限制返回10个结果
            List<User> users = userMapper.pageQueryUser(0, 10, keyword);
            // 清除密码字段
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "搜索用户失败: " + e.getMessage()));
        }
    }

}
