// learning-platform/src/main/java/com/nchu/learningplatform/mapper/CourseMapper.java
package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {

    List<Course> pageQuery(@Param("offset") int offset,
                           @Param("size") int size,
                           @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    Course findById(@Param("id") Long id);
}
