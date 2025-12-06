// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/StatisticsServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getUserLearningStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 获取用户所有选课记录
            List<UserCourse> userCourses = userCourseMapper.findByUserId(userId);
            
            // 统计已加入课程数量
            int courseCount = userCourses.size();
            statistics.put("courseCount", courseCount);
            
            // 统计最近学习时间（最近一次加入课程的时间）
            LocalDateTime latestJoinTime = null;
            if (!userCourses.isEmpty()) {
                latestJoinTime = userCourses.stream()
                        .map(UserCourse::getJoinTime)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
            }
            
            if (latestJoinTime != null) {
                statistics.put("latestJoinTime", latestJoinTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                statistics.put("latestJoinTime", "暂无");
            }
            
            // 统计课程分类分布
            Map<String, Integer> categoryDistribution = new HashMap<>();
            for (UserCourse uc : userCourses) {
                Course course = courseMapper.findById(uc.getCourseId());
                if (course != null && course.getCategory() != null) {
                    String category = course.getCategory();
                    categoryDistribution.put(category, categoryDistribution.getOrDefault(category, 0) + 1);
                }
            }
            statistics.put("categoryDistribution", categoryDistribution);
            
            // 统计课程难度分布
            Map<String, Integer> levelDistribution = new HashMap<>();
            for (UserCourse uc : userCourses) {
                Course course = courseMapper.findById(uc.getCourseId());
                if (course != null && course.getLevel() != null) {
                    String level = course.getLevel();
                    levelDistribution.put(level, levelDistribution.getOrDefault(level, 0) + 1);
                }
            }
            statistics.put("levelDistribution", levelDistribution);
            
            // 按月份统计加入课程数量（最近6个月）
            Map<String, Integer> monthlyJoinCount = new HashMap<>();
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            
            for (UserCourse uc : userCourses) {
                if (uc.getJoinTime() != null && uc.getJoinTime().isAfter(sixMonthsAgo)) {
                    String monthKey = uc.getJoinTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    monthlyJoinCount.put(monthKey, monthlyJoinCount.getOrDefault(monthKey, 0) + 1);
                }
            }
            statistics.put("monthlyJoinCount", monthlyJoinCount);
            
            // 获取课程详情列表（用于前端展示）
            List<Map<String, Object>> courseList = new ArrayList<>();
            for (UserCourse uc : userCourses) {
                Course course = courseMapper.findById(uc.getCourseId());
                if (course != null) {
                    Map<String, Object> courseInfo = new HashMap<>();
                    courseInfo.put("id", course.getId());
                    courseInfo.put("title", course.getTitle());
                    courseInfo.put("category", course.getCategory());
                    courseInfo.put("level", course.getLevel());
                    courseInfo.put("joinTime", uc.getJoinTime() != null 
                            ? uc.getJoinTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) 
                            : null);
                    courseList.add(courseInfo);
                }
            }
            statistics.put("courseList", courseList);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取学习统计失败: " + e.getMessage(), e);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getPlatformLearningStatistics() {
        // 这个方法可以在AdminService中使用，或者合并到AdminService中
        // 暂时返回空Map，可以在AdminServiceImpl中实现
        return new HashMap<>();
    }
}

