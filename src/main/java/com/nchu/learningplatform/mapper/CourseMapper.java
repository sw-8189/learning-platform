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
                           @Param("keyword") String keyword,
                           @Param("learningPreference") String learningPreference,
                           @Param("courseInterest") String courseInterest,
                           @Param("learningGoal") String learningGoal);

    long count(@Param("keyword") String keyword,
               @Param("learningPreference") String learningPreference,
               @Param("courseInterest") String courseInterest,
               @Param("learningGoal") String learningGoal);

    Course findById(@Param("id") Long id);

    // 管理员功能：插入课程
    int insert(Course course);

    // 管理员功能：更新课程
    int update(Course course);

    // 管理员功能：删除课程
    int deleteById(@Param("id") Long id);

    // 管理员功能：分页查询所有课程（不带筛选条件）
    List<Course> pageQueryAll(@Param("offset") int offset, 
                               @Param("size") int size, 
                               @Param("keyword") String keyword);

    // 管理员功能：统计所有课程总数
    long countAll(@Param("keyword") String keyword);
}
