// learning-platform/src/main/java/com/nchu/learningplatform/service/AdminService.java
package com.nchu.learningplatform.service;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService {

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表（分页）
     */
    PageResult<User> getUserList(Integer page, Integer size, String keyword);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 冻结用户
     */
    void freezeUser(Long id);

    /**
     * 恢复用户
     */
    void unfreezeUser(Long id);

    // ==================== 课程管理 ====================

    /**
     * 获取课程列表（分页）
     */
    PageResult<Course> getCourseList(Integer page, Integer size, String keyword);

    /**
     * 添加新课程
     */
    Course addCourse(MultipartFile cover, String title, String description, String detailDescription,
                     String level, String category, String teacher, String teacherIntro,
                     Double price, String tags, String courseOutline, String courseImages,
                     String duration);

    /**
     * 更新课程信息
     */
    Course updateCourse(Long id, MultipartFile cover, String title, String description, String detailDescription,
                        String level, String category, String teacher, String teacherIntro,
                        Double price, String tags, String courseOutline, String courseImages,
                        String duration);

    /**
     * 删除课程
     * @param id 课程ID
     * @param adminId 管理员ID（用于发送通知）
     */
    void deleteCourse(Long id, Long adminId);

    // ==================== 数据统计 ====================

    /**
     * 获取平台统计数据
     */
    Map<String, Object> getPlatformStatistics();

    // ==================== 公告管理 ====================

    /**
     * 发送公告给所有用户
     */
    void sendAnnouncementToAllUsers(String title, String content, Long adminId);
}

