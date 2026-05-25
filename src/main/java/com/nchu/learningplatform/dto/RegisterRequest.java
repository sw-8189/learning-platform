// learning-platform/src/main/java/com/nchu/learningplatform/dto/RegisterRequest.java
package com.nchu.learningplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {

    /** 头像 URL（可空） */
    private String avatarUrl;

    @NotBlank(message = "Username is required")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    /** Gender */
    @NotBlank(message = "Gender is required")
    private String gender;

    /** Learning Preferences (multiple selection) */
    private List<String> learningPreference;

    /** Course Interests (multiple selection) */
    private List<String> courseInterest;

    /** Learning Goal (single selection) */
    @NotBlank(message = "Learning goal is required")
    private String learningGoal;

    /** 用户角色：USER（普通用户）或 ADMIN（管理员），默认为USER */
    private String role;
}
