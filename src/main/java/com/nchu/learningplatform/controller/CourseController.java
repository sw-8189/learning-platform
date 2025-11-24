// learning-platform/src/main/java/com/nchu/learningplatform/controller/CourseController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    @Resource
    private CourseService courseService;

    @Resource
    private AuthController authController;

    @GetMapping
    public PageResult<Course> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer size,
            @RequestParam(required = false) String keyword
    ) {
        return courseService.pageQuery(page, size, keyword);
    }

    @GetMapping("/{id}")
    public Course detail(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录"));
        }
        courseService.joinCourse(userId, id);
        return ResponseEntity.ok(Map.of("message", "已加入我的课程"));
    }
}
