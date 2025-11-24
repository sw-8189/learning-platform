// learning-platform/src/main/java/com/nchu/learningplatform/entity/User.java
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
}
