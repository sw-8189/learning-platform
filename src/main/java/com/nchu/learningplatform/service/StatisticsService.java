// learning-platform/src/main/java/com/nchu/learningplatform/service/StatisticsService.java
package com.nchu.learningplatform.service;

import java.util.Map;

/**
 * 统计数据服务接口
 */
public interface StatisticsService {

    /**
     * 获取用户学习情况统计
     */
    Map<String, Object> getUserLearningStatistics(Long userId);

    /**
     * 获取平台整体学习情况统计（管理员使用）
     */
    Map<String, Object> getPlatformLearningStatistics();
}

