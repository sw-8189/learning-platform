// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/AdminServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Course;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.entity.UserCourse;
import com.nchu.learningplatform.entity.Notification;
import com.nchu.learningplatform.mapper.CommentMapper;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.mapper.NotificationMapper;
import com.nchu.learningplatform.mapper.PostMapper;
import com.nchu.learningplatform.mapper.UserCourseMapper;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.AdminService;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserCourseMapper userCourseMapper;

    @Resource
    private UserService userService;

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentMapper commentMapper;

    @Value("${app.upload.avatar-dir:uploads/avatar}")
    private String avatarUploadDir;

    // 课程封面上传目录
    private static final String COURSE_UPLOAD_DIR = "uploads/course";

    // ==================== 用户管理 ====================

    @Override
    public PageResult<User> getUserList(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<User> users = userMapper.pageQueryUser(offset, size, keyword);
        long total = userMapper.countUser(keyword);
        
        // 清除密码字段（安全考虑）
        users.forEach(user -> user.setPassword(null));
        
        return new PageResult<>(total, page, size, users);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 先删除用户的选课记录
        List<UserCourse> userCourses = userCourseMapper.findByUserId(id);
        for (UserCourse uc : userCourses) {
            userCourseMapper.deleteByUserIdAndCourseId(id, uc.getCourseId());
        }
        // 删除用户
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void freezeUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("不能冻结管理员账号");
        }
        userMapper.updateStatus(id, "FROZEN");
    }

    @Override
    @Transactional
    public void unfreezeUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateStatus(id, "ACTIVE");
    }

    // ==================== 课程管理 ====================

    @Override
    public PageResult<Course> getCourseList(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Course> courses = courseMapper.pageQueryAll(offset, size, keyword);
        long total = courseMapper.countAll(keyword);
        return new PageResult<>(total, page, size, courses);
    }

    @Override
    @Transactional
    public Course addCourse(MultipartFile cover, String title, String description, String detailDescription,
                            String level, String category, String teacher, String teacherIntro,
                            Double price, String tags, String courseOutline, String courseImages,
                            String duration) {
        // 参数验证
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }

        Course course = new Course();
        course.setTitle(title.trim());
        course.setDescription(description);
        course.setDetailDescription(detailDescription);
        course.setLevel(level);
        course.setCategory(category);
        course.setTeacher(teacher);
        course.setTeacherIntro(teacherIntro);
        course.setPrice(price != null ? price : 0.0);
        course.setTags(tags);
        course.setCourseOutline(courseOutline);
        course.setCourseImages(courseImages);
        course.setDuration(duration);
        course.setStudentCount(0);
        course.setRating(0.0);

        // 处理封面图片上传
        if (cover != null && !cover.isEmpty()) {
            try {
                // 验证文件大小（限制 5MB）
                long maxSize = 5 * 1024 * 1024;
                if (cover.getSize() > maxSize) {
                    throw new RuntimeException("封面图片大小不能超过5MB");
                }

                // 验证文件类型
                String originalFilename = cover.getOriginalFilename();
                if (originalFilename != null) {
                    String lowerFilename = originalFilename.toLowerCase();
                    boolean isValidType = lowerFilename.endsWith(".jpg") ||
                            lowerFilename.endsWith(".jpeg") ||
                            lowerFilename.endsWith(".png") ||
                            lowerFilename.endsWith(".gif");
                    if (!isValidType) {
                        throw new RuntimeException("封面图片格式不支持，仅支持 JPG、JPEG、PNG、GIF 格式");
                    }
                }

                // 创建课程封面上传目录
                Path courseDir = Paths.get(COURSE_UPLOAD_DIR);
                if (!courseDir.isAbsolute()) {
                    courseDir = Paths.get(System.getProperty("user.dir")).resolve(COURSE_UPLOAD_DIR);
                }
                Files.createDirectories(courseDir);

                // 生成文件名
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
                }
                String filename = UUID.randomUUID().toString() + ext;
                Path target = courseDir.resolve(filename);

                // 保存文件
                java.io.File targetFile = target.toFile();
                if (targetFile == null) {
                    throw new RuntimeException("无法创建目标文件");
                }
                cover.transferTo(targetFile);
                course.setCoverUrl("/uploads/course/" + filename);
            } catch (Exception e) {
                throw new RuntimeException("上传封面图片失败: " + e.getMessage(), e);
            }
        }

        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, MultipartFile cover, String title, String description, String detailDescription,
                               String level, String category, String teacher, String teacherIntro,
                               Double price, String tags, String courseOutline, String courseImages,
                               String duration) {
        Course existingCourse = courseMapper.findById(id);
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }

        // 更新课程信息
        if (title != null && !title.trim().isEmpty()) {
            existingCourse.setTitle(title.trim());
        }
        if (description != null) {
            existingCourse.setDescription(description);
        }
        if (detailDescription != null) {
            existingCourse.setDetailDescription(detailDescription);
        }
        if (level != null) {
            existingCourse.setLevel(level);
        }
        if (category != null) {
            existingCourse.setCategory(category);
        }
        if (teacher != null) {
            existingCourse.setTeacher(teacher);
        }
        if (teacherIntro != null) {
            existingCourse.setTeacherIntro(teacherIntro);
        }
        if (price != null) {
            existingCourse.setPrice(price);
        }
        if (tags != null) {
            existingCourse.setTags(tags);
        }
        if (courseOutline != null) {
            existingCourse.setCourseOutline(courseOutline);
        }
        if (courseImages != null) {
            existingCourse.setCourseImages(courseImages);
        }
        if (duration != null) {
            existingCourse.setDuration(duration);
        }

        // 处理封面图片更新
        if (cover != null && !cover.isEmpty()) {
            try {
                // 验证文件大小和类型（与添加课程相同）
                long maxSize = 5 * 1024 * 1024;
                if (cover.getSize() > maxSize) {
                    throw new RuntimeException("封面图片大小不能超过5MB");
                }

                String originalFilename = cover.getOriginalFilename();
                if (originalFilename != null) {
                    String lowerFilename = originalFilename.toLowerCase();
                    boolean isValidType = lowerFilename.endsWith(".jpg") ||
                            lowerFilename.endsWith(".jpeg") ||
                            lowerFilename.endsWith(".png") ||
                            lowerFilename.endsWith(".gif");
                    if (!isValidType) {
                        throw new RuntimeException("封面图片格式不支持，仅支持 JPG、JPEG、PNG、GIF 格式");
                    }
                }

                // 创建课程封面上传目录
                Path courseDir = Paths.get(COURSE_UPLOAD_DIR);
                if (!courseDir.isAbsolute()) {
                    courseDir = Paths.get(System.getProperty("user.dir")).resolve(COURSE_UPLOAD_DIR);
                }
                Files.createDirectories(courseDir);

                // 生成新文件名
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
                }
                String filename = UUID.randomUUID().toString() + ext;
                Path target = courseDir.resolve(filename);

                // 保存新文件
                File targetFile = target.toFile();
                if (targetFile != null) {
                    cover.transferTo(targetFile);
                } else {
                    throw new RuntimeException("无法创建目标文件");
                }
                existingCourse.setCoverUrl("/uploads/course/" + filename);
            } catch (Exception e) {
                throw new RuntimeException("更新封面图片失败: " + e.getMessage(), e);
            }
        }

        courseMapper.update(existingCourse);
        return existingCourse;
    }

    @Override
    @Transactional
    public void deleteCourse(Long id, Long adminId) {
        Course course = courseMapper.findById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 1. 查询所有选择了这门课程的用户
        List<UserCourse> userCourses = userCourseMapper.findByCourseId(id);
        List<Long> userIds = new ArrayList<>();
        for (UserCourse uc : userCourses) {
            userIds.add(uc.getUserId());
        }
        
        // 2. 如果有用户选择了这门课程，发送系统公告通知他们
        if (!userIds.isEmpty()) {
            String title = "课程下架通知";
            String content = String.format("很抱歉，您选择的课程《%s》已被管理员下架。\n\n" +
                    "如有疑问，请联系平台客服。感谢您的理解与支持！", course.getTitle());
            
            // 为每个选择了该课程的用户发送通知
            for (Long userId : userIds) {
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setType("announcement");
                notification.setRelatedId(id); // 关联课程ID
                notification.setFromUserId(adminId);
                notification.setContent(title + "\n\n" + content);
                notification.setIsRead(false);
                notificationMapper.insert(notification);
            }
        }
        
        // 3. 删除所有用户选课记录
        userCourseMapper.deleteByCourseId(id);
        
        // 4. 删除课程
        int deleted = courseMapper.deleteById(id);
        if (deleted == 0) {
            throw new RuntimeException("删除课程失败");
        }
    }

    // ==================== 数据统计 ====================

    @Override
    public Map<String, Object> getPlatformStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 统计用户总数
            long totalUsers = userMapper.countUser(null);
            statistics.put("totalUsers", totalUsers);
            
            // 统计管理员数量
            // 需要扩展Mapper添加统计管理员的方法，这里先用简单方式
            List<User> allUsers = userMapper.pageQueryUser(0, Integer.MAX_VALUE, null);
            long adminCount = allUsers.stream()
                    .filter(user -> "ADMIN".equals(user.getRole()))
                    .count();
            statistics.put("adminCount", adminCount);
            
            // 统计普通用户数量
            statistics.put("normalUserCount", totalUsers - adminCount);
            
            // 统计课程总数
            long totalCourses = courseMapper.countAll(null);
            statistics.put("totalCourses", totalCourses);

            // 统计帖子与评论总数
            long totalPosts = postMapper.countAll();
            statistics.put("totalPosts", totalPosts);
            long totalComments = commentMapper.countAll();
            statistics.put("totalComments", totalComments);
            
            // 统计选课总数
            // 由于没有直接的统计方法，需要查询所有用户选课记录
            long totalEnrollments = 0;
            for (User user : allUsers) {
                List<UserCourse> userCourses = userCourseMapper.findByUserId(user.getId());
                totalEnrollments += userCourses.size();
            }
            statistics.put("totalEnrollments", totalEnrollments);
            
            // 统计课程分类分布
            List<Course> allCourses = courseMapper.pageQueryAll(0, Integer.MAX_VALUE, null);
            Map<String, Long> categoryDistribution = new HashMap<>();
            for (Course course : allCourses) {
                String cat = course.getCategory() != null ? course.getCategory() : "未分类";
                categoryDistribution.put(cat, categoryDistribution.getOrDefault(cat, 0L) + 1);
            }
            statistics.put("categoryDistribution", categoryDistribution);
            
            // 统计课程难度分布
            Map<String, Long> levelDistribution = new HashMap<>();
            for (Course course : allCourses) {
                String level = course.getLevel() != null ? course.getLevel() : "未分类";
                levelDistribution.put(level, levelDistribution.getOrDefault(level, 0L) + 1);
            }
            statistics.put("levelDistribution", levelDistribution);
            
            // 统计学习偏好分布
            Map<String, Long> preferenceDistribution = new HashMap<>();
            for (User user : allUsers) {
                if (user.getLearningPreference() != null && !user.getLearningPreference().isEmpty()) {
                    String[] preferences = user.getLearningPreference().split(",");
                    for (String pref : preferences) {
                        String trimmed = pref.trim();
                        if (!trimmed.isEmpty()) {
                            preferenceDistribution.put(trimmed, 
                                    preferenceDistribution.getOrDefault(trimmed, 0L) + 1);
                        }
                    }
                }
            }
            statistics.put("preferenceDistribution", preferenceDistribution);
            
            // 统计课程兴趣分布
            Map<String, Long> interestDistribution = new HashMap<>();
            for (User user : allUsers) {
                if (user.getCourseInterest() != null && !user.getCourseInterest().isEmpty()) {
                    String[] interests = user.getCourseInterest().split(",");
                    for (String interest : interests) {
                        String trimmed = interest.trim();
                        if (!trimmed.isEmpty()) {
                            interestDistribution.put(trimmed, 
                                    interestDistribution.getOrDefault(trimmed, 0L) + 1);
                        }
                    }
                }
            }
            statistics.put("interestDistribution", interestDistribution);
            
            // 统计最受欢迎的课程（按选课人数）
            Map<Long, Long> courseEnrollmentCount = new HashMap<>();
            for (User user : allUsers) {
                List<UserCourse> userCourses = userCourseMapper.findByUserId(user.getId());
                for (UserCourse uc : userCourses) {
                    courseEnrollmentCount.put(uc.getCourseId(),
                            courseEnrollmentCount.getOrDefault(uc.getCourseId(), 0L) + 1);
                }
            }
            
            // 获取选课数最多的前5个课程
            List<Map<String, Object>> popularCourses = new ArrayList<>();
            courseEnrollmentCount.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .forEach(entry -> {
                        Course course = courseMapper.findById(entry.getKey());
                        if (course != null) {
                            Map<String, Object> courseInfo = new HashMap<>();
                            courseInfo.put("id", course.getId());
                            courseInfo.put("title", course.getTitle());
                            courseInfo.put("enrollmentCount", entry.getValue());
                            popularCourses.add(courseInfo);
                        }
                    });
            statistics.put("popularCourses", popularCourses);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取统计数据失败: " + e.getMessage(), e);
        }
        
        return statistics;
    }

    // ==================== 公告管理 ====================

    @Override
    @Transactional
    public void sendAnnouncementToAllUsers(String title, String content, Long adminId) {
        // 分页获取所有用户（排除管理员自己）
        int pageSize = 100; // 每页100个用户
        int currentPage = 1;
        boolean hasMore = true;
        
        while (hasMore) {
            int offset = (currentPage - 1) * pageSize;
            List<User> users = userMapper.pageQueryUser(offset, pageSize, null);
            
            if (users.isEmpty()) {
                hasMore = false;
            } else {
                // 为每个用户创建通知
                for (User user : users) {
                    if (!user.getId().equals(adminId)) { // 不给自己发通知
                        Notification notification = new Notification();
                        notification.setUserId(user.getId());
                        notification.setType("announcement"); // 公告类型
                        notification.setRelatedId(null);
                        notification.setFromUserId(adminId);
                        notification.setContent(title + "\n\n" + content); // 标题和内容合并
                        notification.setIsRead(false);
                        notificationMapper.insert(notification);
                    }
                }
                
                // 如果返回的用户数少于pageSize，说明已经是最后一页
                if (users.size() < pageSize) {
                    hasMore = false;
                } else {
                    currentPage++;
                }
            }
        }
    }
}

