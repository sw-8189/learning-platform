// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/UserServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_AVATAR = "/image/avatar/default-avatar.png";

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 参数验证
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            throw new RuntimeException("Gender is required");
        }
        if (request.getLearningGoal() == null || request.getLearningGoal().trim().isEmpty()) {
            throw new RuntimeException("Learning goal is required");
        }
        if (request.getLearningPreference() == null || request.getLearningPreference().isEmpty()) {
            throw new RuntimeException("Please select at least one learning preference");
        }
        if (request.getCourseInterest() == null || request.getCourseInterest().isEmpty()) {
            throw new RuntimeException("Please select at least one course interest");
        }

        // Unique check
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (userMapper.findByPhone(request.getPhone()) != null) {
            throw new RuntimeException("Phone number is already registered");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        // 设置角色，如果未指定则默认为USER
        user.setRole(request.getRole() != null && !request.getRole().trim().isEmpty() 
                    ? request.getRole().trim().toUpperCase() 
                    : "USER");
        user.setGender(request.getGender());

        // 头像：前端没传就用默认
        if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(DEFAULT_AVATAR);
        } else {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        // 设置用户状态为正常
        user.setStatus("ACTIVE");

        user.setLearningPreference(joinList(request.getLearningPreference()));
        user.setCourseInterest(joinList(request.getCourseInterest()));
        user.setLearningGoal(request.getLearningGoal());

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // 处理并发情况下的唯一性约束冲突
            // 这种情况发生在：两个请求同时通过唯一性检查，然后都尝试插入
            // 数据库的唯一性约束会阻止第二个插入
            String exceptionMessage = e.getMessage();
            if (exceptionMessage != null) {
                String lowerMessage = exceptionMessage.toLowerCase();
                if (lowerMessage.contains("username") || lowerMessage.contains("'user.username'")) {
                    throw new RuntimeException("Username already exists");
                } else if (lowerMessage.contains("phone") || lowerMessage.contains("'user.phone'")) {
                    throw new RuntimeException("Phone number is already registered");
                } else if (lowerMessage.contains("email") || lowerMessage.contains("'user.email'")) {
                    throw new RuntimeException("Email is already registered");
                }
            }
            // 如果无法确定具体字段，抛出通用错误
            throw new RuntimeException("Registration conflict. Please check if username, phone, or email is already in use");
        } catch (Exception e) {
            // 检查是否是SQL唯一性约束冲突
            Throwable cause = e.getCause();
            if (cause != null) {
                String causeMessage = cause.getMessage();
                if (causeMessage != null && causeMessage.contains("Duplicate entry")) {
                    String lowerMessage = causeMessage.toLowerCase();
                    if (lowerMessage.contains("username") || lowerMessage.contains("'user.username'")) {
                        throw new RuntimeException("用户名已存在");
                    } else if (lowerMessage.contains("phone") || lowerMessage.contains("'user.phone'")) {
                        throw new RuntimeException("手机号已被注册");
                    } else if (lowerMessage.contains("email") || lowerMessage.contains("'user.email'")) {
                        throw new RuntimeException("邮箱已被注册");
                    }
                }
            }
            // 重新抛出原始异常
            throw e;
        }
    }

    @Override
    public User login(LoginRequest request) {
        User user = userMapper.findByUsernameOrPhone(request.getUsernameOrPhone());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        // 检查用户状态
        if ("FROZEN".equals(user.getStatus())) {
            throw new RuntimeException("Account has been frozen. Please contact administrator");
        }
        return user;
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    @Transactional
    public void updateUserInfo(User user) {
        // 更新用户信息（除手机号外）
        userMapper.updateUserInfo(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Incorrect current password");
        }
        userMapper.updatePassword(userId, newPassword);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email is not registered");
        }
        userMapper.updatePassword(user.getId(), newPassword);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.findByEmail(email) != null;
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String s : list) {
            joiner.add(s);
        }
        return joiner.toString();
    }
}
