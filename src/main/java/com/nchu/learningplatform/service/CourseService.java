// learning-platform/src/main/java/com/nchu/learningplatform/service/CourseService.java
package com.nchu.learningplatform.service;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;

public interface CourseService {

    PageResult<Course> pageQuery(Integer page, Integer size, String keyword);

    Course getById(Long id);

    void joinCourse(Long userId, Long courseId);
}
