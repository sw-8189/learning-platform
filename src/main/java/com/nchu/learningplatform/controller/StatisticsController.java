// learning-platform/src/main/java/com/nchu/learningplatform/controller/StatisticsController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @Resource
    private AuthController authController;

    /**
     * 获取用户学习情况统计
     */
    @GetMapping("/user/learning")
    public ResponseEntity<?> getUserLearningStatistics(
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }

        try {
            Map<String, Object> statistics = statisticsService.getUserLearningStatistics(userId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "获取学习统计失败: " + e.getMessage()));
        }
    }
}

