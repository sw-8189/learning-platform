// learning-platform/src/main/java/com/nchu/learningplatform/service/UserService.java
package com.nchu.learningplatform.service;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;

public interface UserService {

    void register(RegisterRequest request);

    User login(LoginRequest request);

    User getById(Long id);

    void updateUserInfo(User user);

    void updatePassword(Long userId, String oldPassword, String newPassword);
}
