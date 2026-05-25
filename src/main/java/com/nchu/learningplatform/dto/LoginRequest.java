// learning-platform/src/main/java/com/nchu/learningplatform/dto/LoginRequest.java
package com.nchu.learningplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username or phone number is required")
    private String usernameOrPhone;

    @NotBlank(message = "Password is required")
    private String password;
}
