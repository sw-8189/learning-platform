// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/CourseServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private UserMapper userMapper;

    public PageResult<Course> pageQuery(Integer page, Integer size, String keyword) {
        return pageQuery(page, size, keyword, null, null, null);
    }

    @Override
    public PageResult<Course> pageQuery(Integer page, Integer size, String keyword, String learningPreference, String courseInterest, String learningGoal) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 9;
        int offset = (page - 1) * size;

        List<Course> list = courseMapper.pageQuery(offset, size, keyword, learningPreference, courseInterest, learningGoal);
        long total = courseMapper.count(keyword, learningPreference, courseInterest, learningGoal);

        return new PageResult<>(total, page, size, list);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    @Transactional
    public void joinCourse(Long userId, Long courseId) {
        // 检查是否已经加入
        List<UserCourse> existing = userCourseMapper.findByUserId(userId);
        for (UserCourse uc : existing) {
            if (uc.getCourseId().equals(courseId)) {
                throw new RuntimeException("您已经加入过该课程");
            }
        }

        UserCourse uc = new UserCourse();
        uc.setUserId(userId);
        uc.setCourseId(courseId);
        uc.setJoinTime(LocalDateTime.now());
        userCourseMapper.insert(uc);
    }

    @Override
    @Transactional
    public void quitCourse(Long userId, Long courseId) {
        int deleted = userCourseMapper.deleteByUserIdAndCourseId(userId, courseId);
        if (deleted == 0) {
            throw new RuntimeException("您未加入该课程");
        }
    }

    @Override
    public PageResult<Course> getRecommendedCourses(Long userId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 9;
        int offset = (page - 1) * size;

        // 获取用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            return new PageResult<>(0, page, size, Collections.emptyList());
        }

        // 获取用户已加入的课程ID
        List<UserCourse> userCourses = userCourseMapper.findByUserId(userId);
        Set<Long> joinedCourseIds = userCourses.stream()
                .map(UserCourse::getCourseId)
                .collect(Collectors.toSet());

        // 获取所有课程
        List<Course> allCourses = courseMapper.pageQuery(0, 1000, null, null, null, null);

        // 根据用户标签推荐课程（学习偏好、课程兴趣、学习目标）
        List<Course> recommended = new ArrayList<>();
        String learningPreference = user.getLearningPreference();
        String courseInterest = user.getCourseInterest();
        String learningGoal = user.getLearningGoal();

        // 构建用户标签集合
        Set<String> userTags = new HashSet<>();
        if (learningPreference != null && !learningPreference.isEmpty()) {
            Collections.addAll(userTags, learningPreference.split(","));
        }
        if (courseInterest != null && !courseInterest.isEmpty()) {
            Collections.addAll(userTags, courseInterest.split(","));
        }
        if (learningGoal != null && !learningGoal.isEmpty()) {
            userTags.add(learningGoal);
        }

        // 根据标签匹配课程
        for (Course course : allCourses) {
            if (joinedCourseIds.contains(course.getId())) {
                continue; // 跳过已加入的课程
            }
            
            if (course.getTags() != null && !course.getTags().isEmpty()) {
                String[] courseTags = course.getTags().split(",");
                // 计算匹配度（匹配的标签数量）
                int matchCount = 0;
                for (String courseTag : courseTags) {
                    String trimmedTag = courseTag.trim();
                    if (userTags.contains(trimmedTag)) {
                        matchCount++;
                    }
                }
                // 如果至少匹配一个标签，则推荐
                if (matchCount > 0) {
                    recommended.add(course);
                }
            }
        }

        // 按匹配度排序（匹配标签多的排在前面）
        recommended.sort((c1, c2) -> {
            int count1 = getMatchCount(c1.getTags(), userTags);
            int count2 = getMatchCount(c2.getTags(), userTags);
            return Integer.compare(count2, count1); // 降序
        });

        // 如果没有推荐，返回所有未加入的课程
        if (recommended.isEmpty()) {
            recommended = allCourses.stream()
                    .filter(c -> !joinedCourseIds.contains(c.getId()))
                    .collect(Collectors.toList());
        }

        // 去重
        recommended = recommended.stream()
                .distinct()
                .collect(Collectors.toList());

        // 分页
        long total = recommended.size();
        int fromIndex = Math.min(offset, recommended.size());
        int toIndex = Math.min(offset + size, recommended.size());
        List<Course> pagedCourses = fromIndex < toIndex ? recommended.subList(fromIndex, toIndex) : Collections.emptyList();

        return new PageResult<>(total, page, size, pagedCourses);
    }

    private int getMatchCount(String courseTags, Set<String> userTags) {
        if (courseTags == null || courseTags.isEmpty()) {
            return 0;
        }
        int count = 0;
        String[] tags = courseTags.split(",");
        for (String tag : tags) {
            if (userTags.contains(tag.trim())) {
                count++;
            }
        }
        return count;
    }
}
