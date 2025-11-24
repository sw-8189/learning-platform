// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/CourseServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Override
    public PageResult<Course> pageQuery(Integer page, Integer size, String keyword) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 8;
        int offset = (page - 1) * size;

        List<Course> list = courseMapper.pageQuery(offset, size, keyword);
        long total = courseMapper.count(keyword);

        return new PageResult<>(total, page, size, list);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    @Transactional
    public void joinCourse(Long userId, Long courseId) {
        UserCourse uc = new UserCourse();
        uc.setUserId(userId);
        uc.setCourseId(courseId);
        uc.setJoinTime(LocalDateTime.now());
        userCourseMapper.insert(uc);
    }
}
