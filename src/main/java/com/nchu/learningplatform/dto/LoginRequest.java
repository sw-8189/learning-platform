// learning-platform/src/main/java/com/nchu/learningplatform/dto/LoginRequest.java
package com.nchu.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "用户名或手机号不能为空")
    private String usernameOrPhone;

    @NotBlank(message = "密码不能为空")
    private String password;
}
