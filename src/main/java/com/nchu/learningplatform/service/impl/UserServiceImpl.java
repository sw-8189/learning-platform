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
        // 唯一性校验
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.findByPhone(request.getPhone()) != null) {
            throw new RuntimeException("手机号已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setGender(request.getGender());

        // 头像：前端没传就用默认
        if (request.getAvatarUrl() == null || request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(DEFAULT_AVATAR);
        } else {
            user.setAvatarUrl(request.getAvatarUrl());
        }

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
