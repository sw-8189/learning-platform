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

    @NotBlank(message = "用户名不能为空")
    private String username;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 性别 */
    @NotBlank(message = "性别不能为空")
    private String gender;

    /** 学习偏好（可多选） */
    private List<String> learningPreference;

    /** 课程兴趣（可多选） */
    private List<String> courseInterest;

    /** 学习目标（单选） */
    @NotBlank(message = "学习目标不能为空")
    private String learningGoal;

    /** 用户角色：USER（普通用户）或 ADMIN（管理员），默认为USER */
    private String role;
}
