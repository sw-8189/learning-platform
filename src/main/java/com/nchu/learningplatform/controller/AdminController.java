// learning-platform/src/main/java/com/nchu/learningplatform/controller/AdminController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员功能接口：用户管理、课程管理、数据统计
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private AuthController authController;

    /**
     * 验证管理员权限
     */
    private boolean checkAdminPermission(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return false;
        }
        User user = adminService.getUserById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUserList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            PageResult<User> result = adminService.getUserList(page, size, keyword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch user list: " + e.getMessage()));
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            User user = adminService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }
            // 不返回密码
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch user details: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * 冻结用户
     */
    @PutMapping("/users/{id}/freeze")
    public ResponseEntity<?> freezeUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            adminService.freezeUser(id);
            return ResponseEntity.ok(Map.of("message", "User has been frozen"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to freeze user: " + e.getMessage()));
        }
    }

    /**
     * 恢复用户
     */
    @PutMapping("/users/{id}/unfreeze")
    public ResponseEntity<?> unfreezeUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            adminService.unfreezeUser(id);
            return ResponseEntity.ok(Map.of("message", "User has been restored"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to restore user: " + e.getMessage()));
        }
    }

    // ==================== 课程管理 ====================

    /**
     * 获取所有课程列表（分页）
     */
    @GetMapping("/courses")
    public ResponseEntity<?> getCourseList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            PageResult<Course> result = adminService.getCourseList(page, size, keyword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch course list: " + e.getMessage()));
        }
    }

    /**
     * 添加新课程
     */
    @PostMapping("/courses")
    public ResponseEntity<?> addCourse(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) MultipartFile cover,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String detailDescription,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String teacherIntro,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String courseOutline,
            @RequestParam(required = false) String courseImages,
            @RequestParam(required = false) String duration
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            Course course = adminService.addCourse(cover, title, description, detailDescription,
                    level, category, teacher, teacherIntro, price, tags, courseOutline,
                    courseImages, duration);
            return ResponseEntity.ok(Map.of("message", "Course added successfully", "course", course));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to add course: " + e.getMessage()));
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile cover,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String detailDescription,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String teacherIntro,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String courseOutline,
            @RequestParam(required = false) String courseImages,
            @RequestParam(required = false) String duration
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            Course course = adminService.updateCourse(id, cover, title, description, detailDescription,
                    level, category, teacher, teacherIntro, price, tags, courseOutline,
                    courseImages, duration);
            return ResponseEntity.ok(Map.of("message", "Course updated successfully", "course", course));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update course: " + e.getMessage()));
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            Long adminId = authController.getUserIdByToken(token);
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Unable to retrieve admin information"));
            }
            adminService.deleteCourse(id, adminId);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully. Affected users have been notified"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete course: " + e.getMessage()));
        }
    }

    // ==================== 数据统计 ====================

    /**
     * 获取平台统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            Map<String, Object> statistics = adminService.getPlatformStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch statistics: " + e.getMessage()));
        }
    }

    // ==================== 公告管理 ====================

    /**
     * 发送公告给所有用户
     */
    @PostMapping("/announcement")
    public ResponseEntity<?> sendAnnouncement(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> payload) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        if (!checkAdminPermission(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin privileges required"));
        }

        try {
            String title = payload.get("title");
            String content = payload.get("content");

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Announcement title cannot be empty"));
            }

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Announcement content cannot be empty"));
            }

            Long adminId = authController.getUserIdByToken(token);
            adminService.sendAnnouncementToAllUsers(title.trim(), content.trim(), adminId);

            return ResponseEntity.ok(Map.of("message", "Announcement sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send announcement: " + e.getMessage()));
        }
    }
}

