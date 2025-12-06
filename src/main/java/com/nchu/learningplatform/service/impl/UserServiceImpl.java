// learning-platform/src/main/java/com/nchu/learningplatform/service/impl/UserServiceImpl.java
package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
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
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            throw new RuntimeException("性别不能为空");
        }
        if (request.getLearningGoal() == null || request.getLearningGoal().trim().isEmpty()) {
            throw new RuntimeException("学习目标不能为空");
        }
        if (request.getLearningPreference() == null || request.getLearningPreference().isEmpty()) {
            throw new RuntimeException("请至少选择一个学习偏好");
        }
        if (request.getCourseInterest() == null || request.getCourseInterest().isEmpty()) {
            throw new RuntimeException("请至少选择一个课程兴趣");
        }

        // 唯一性校验
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.findByPhone(request.getPhone()) != null) {
            throw new RuntimeException("手机号已被注册");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
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

        userMapper.insert(user);
    }

    @Override
    public User login(LoginRequest request) {
        User user = userMapper.findByUsernameOrPhone(request.getUsernameOrPhone());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 检查用户状态
        if ("FROZEN".equals(user.getStatus())) {
            throw new RuntimeException("账号已被冻结，请联系管理员");
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
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        userMapper.updatePassword(userId, newPassword);
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
