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

    private static final Map<String, String> TAG_ALIASES = buildTagAliases();

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

        String learningPreferenceAlt = getTagAlias(learningPreference);
        String courseInterestAlt = getTagAlias(courseInterest);
        String learningGoalAlt = getTagAlias(learningGoal);

        List<Course> list = courseMapper.pageQuery(
                offset,
                size,
                keyword,
                normalizeFilter(learningPreference),
                learningPreferenceAlt,
                normalizeFilter(courseInterest),
                courseInterestAlt,
                normalizeFilter(learningGoal),
                learningGoalAlt
        );
        long total = courseMapper.count(
                keyword,
                normalizeFilter(learningPreference),
                learningPreferenceAlt,
                normalizeFilter(courseInterest),
                courseInterestAlt,
                normalizeFilter(learningGoal),
                learningGoalAlt
        );

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
                throw new RuntimeException("You have already joined this course.");
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
            throw new RuntimeException("You have not joined this course.");
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
        List<Course> allCourses = courseMapper.pageQuery(0, 1000, null, null, null, null, null, null, null);

        // 根据用户标签推荐课程（学习偏好、课程兴趣、学习目标）
        List<Course> recommended = new ArrayList<>();
        String learningPreference = user.getLearningPreference();
        String courseInterest = user.getCourseInterest();
        String learningGoal = user.getLearningGoal();

        // 构建用户标签集合
        Set<String> userTags = new HashSet<>();
        addExpandedTags(userTags, learningPreference);
        addExpandedTags(userTags, courseInterest);
        addExpandedTags(userTags, learningGoal);

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

    private static Map<String, String> buildTagAliases() {
        Map<String, String> aliases = new HashMap<>();
        addAlias(aliases, "Male", "男");
        addAlias(aliases, "Female", "女");
        addAlias(aliases, "Visual Learning", "视觉学习");
        addAlias(aliases, "Auditory Learning", "听觉学习");
        addAlias(aliases, "Hands-on Practice", "动手实践");
        addAlias(aliases, "Reading & Writing", "阅读写作");
        addAlias(aliases, "Social Learning", "社交学习");
        addAlias(aliases, "Independent Learning", "独立学习");
        addAlias(aliases, "Programming Development", "编程开发");
        addAlias(aliases, "Frontend Development", "前端开发");
        addAlias(aliases, "Frontend Development", "前端学习");
        addAlias(aliases, "Backend Development", "后端开发");
        addAlias(aliases, "Data Science", "数据科学");
        addAlias(aliases, "Artificial Intelligence", "人工智能");
        addAlias(aliases, "Design & Creativity", "设计创意");
        addAlias(aliases, "Business Management", "商业管理");
        addAlias(aliases, "Professional Skills", "职业技能");
        addAlias(aliases, "Career Advancement", "职业提升");
        addAlias(aliases, "Skill Expansion", "技能拓展");
        addAlias(aliases, "Academic Advancement", "学术深造");
        addAlias(aliases, "Hobby & Interest", "兴趣爱好");
        return Collections.unmodifiableMap(aliases);
    }

    private static void addAlias(Map<String, String> aliases, String english, String legacyChinese) {
        aliases.put(english, legacyChinese);
        aliases.put(legacyChinese, english);
    }

    private String normalizeFilter(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String getTagAlias(String value) {
        String normalized = normalizeFilter(value);
        return normalized == null ? null : TAG_ALIASES.get(normalized);
    }

    private void addExpandedTags(Set<String> tags, String rawValues) {
        if (rawValues == null || rawValues.isBlank()) {
            return;
        }
        for (String value : rawValues.split(",")) {
            String normalized = normalizeFilter(value);
            if (normalized == null) {
                continue;
            }
            tags.add(normalized);
            String alias = getTagAlias(normalized);
            if (alias != null && !alias.isBlank()) {
                tags.add(alias);
            }
        }
    }
}
