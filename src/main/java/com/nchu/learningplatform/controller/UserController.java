// learning-platform/src/main/java/com/nchu/learningplatform/controller/UserController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
