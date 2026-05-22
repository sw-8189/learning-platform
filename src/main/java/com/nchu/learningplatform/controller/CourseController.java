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
            @RequestParam(defaultValue = "9") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String learningPreference,
            @RequestParam(required = false) String courseInterest,
            @RequestParam(required = false) String learningGoal
    ) {
        return courseService.pageQuery(page, size, keyword, learningPreference, courseInterest, learningGoal);
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
            return ResponseEntity.status(401).body(Map.of("message", "Please sign in first."));
        }
        try {
            courseService.joinCourse(userId, id);
            return ResponseEntity.ok(Map.of("message", "Course added to My Courses."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/quit")
    public ResponseEntity<?> quit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please sign in first."));
        }
        try {
            courseService.quitCourse(userId, id);
            return ResponseEntity.ok(Map.of("message", "Course removed from My Courses."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/recommended")
    public PageResult<Course> getRecommended(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "9") Integer size
    ) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return new PageResult<>(0, page, size, java.util.Collections.emptyList());
        }
        return courseService.getRecommendedCourses(userId, page, size);
    }
}
