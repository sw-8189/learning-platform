// learning-platform/src/main/java/com/nchu/learningplatform/mapper/UserCourseMapper.java
package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCourseMapper {

    int insert(UserCourse userCourse);

    List<UserCourse> findByUserId(@Param("userId") Long userId);

    List<UserCourse> findByCourseId(@Param("courseId") Long courseId);

    int deleteByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    int deleteByCourseId(@Param("courseId") Long courseId);
}
