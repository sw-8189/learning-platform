package com.nchu.learningplatform.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatarUrl;
    private String role;
    private String gender;
    private String learningPreference;
    private String courseInterest;
    private String learningGoal;
    private String status; // 用户状态：ACTIVE(正常), FROZEN(冻结)
}
