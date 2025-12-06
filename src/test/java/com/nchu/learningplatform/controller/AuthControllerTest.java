package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.service.EmailCodeService;
import com.nchu.learningplatform.service.UserService;
import com.nchu.learningplatform.util.MailUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 单元测试
 * 测试认证控制器的各种功能
 */
@WebMvcTest(AuthController.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private UserService userService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private EmailCodeService emailCodeService;

    @org.springframework.boot.test.mock.mockito.MockBean
    private MailUtils mailUtils;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setPassword("test123");
        testUser.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("测试发送验证码 - 正常场景：邮箱格式正确且未注册")
    void testSendCode_ValidEmail() throws Exception {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        when(userService.existsByEmail(email)).thenReturn(false);
        when(emailCodeService.canSendCode(email)).thenReturn(true);
        when(emailCodeService.generateCode()).thenReturn(code);
        when(emailCodeService.saveCode(email, code)).thenReturn(true);
        when(mailUtils.sendMail(eq(email), anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/send-code")
                .param("email", email)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码已发送到您的邮箱，请查收"));

        verify(emailCodeService, times(1)).generateCode();
        verify(emailCodeService, times(1)).saveCode(email, code);
        verify(mailUtils, times(1)).sendMail(eq(email), anyString(), anyString());
    }

    @Test
    @DisplayName("测试发送验证码 - 异常情况：邮箱格式不正确")
    void testSendCode_InvalidEmailFormat() throws Exception {
        // Arrange
        String email = "invalid-email";

        // Act & Assert
        mockMvc.perform(post("/api/auth/send-code")
                .param("email", email)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));

        verify(emailCodeService, never()).generateCode();
        verify(mailUtils, never()).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("测试发送验证码 - 异常情况：邮箱为空")
    void testSendCode_EmptyEmail() throws Exception {
        // Arrange
        String email = "";

        // Act & Assert
        mockMvc.perform(post("/api/auth/send-code")
                .param("email", email)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));
    }

    @Test
    @DisplayName("测试发送验证码 - 异常情况：邮箱已被注册")
    void testSendCode_EmailAlreadyRegistered() throws Exception {
        // Arrange
        String email = "existing@example.com";

        when(userService.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/send-code")
                .param("email", email)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("该邮箱已被注册"));

        verify(emailCodeService, never()).generateCode();
    }

    @Test
    @DisplayName("测试发送验证码 - 异常情况：发送频率过高")
    void testSendCode_TooFrequent() throws Exception {
        // Arrange
        String email = "test@example.com";

        when(userService.existsByEmail(email)).thenReturn(false);
        when(emailCodeService.canSendCode(email)).thenReturn(false);
        when(emailCodeService.getRemainingWaitTime(email)).thenReturn(30L);

        // Act & Assert
        mockMvc.perform(post("/api/auth/send-code")
                .param("email", email)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.remainingTime").value(30));

        verify(emailCodeService, never()).generateCode();
    }

    @Test
    @DisplayName("测试检查验证码 - 正常场景：验证码正确")
    void testCheckCode_ValidCode() throws Exception {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        when(emailCodeService.checkCode(email, code)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/check-code")
                .param("email", email)
                .param("emailCode", code)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码正确"));

        verify(emailCodeService, times(1)).checkCode(email, code);
    }

    @Test
    @DisplayName("测试检查验证码 - 异常情况：验证码错误")
    void testCheckCode_InvalidCode() throws Exception {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        when(emailCodeService.checkCode(email, code)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/check-code")
                .param("email", email)
                .param("emailCode", code)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("验证码错误或已过期，请检查后重试"));
    }

    @Test
    @DisplayName("测试检查验证码 - 异常情况：邮箱为空")
    void testCheckCode_EmptyEmail() throws Exception {
        // Arrange
        String email = "";
        String code = "123456";

        // Act & Assert
        mockMvc.perform(post("/api/auth/check-code")
                .param("email", email)
                .param("emailCode", code)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("邮箱不能为空"));
    }

    @Test
    @DisplayName("测试检查验证码 - 异常情况：验证码为空")
    void testCheckCode_EmptyCode() throws Exception {
        // Arrange
        String email = "test@example.com";
        String code = "";

        // Act & Assert
        mockMvc.perform(post("/api/auth/check-code")
                .param("email", email)
                .param("emailCode", code)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("验证码不能为空"));
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：所有参数有效")
    void testRegister_ValidRequest() throws Exception {
        // Arrange
        MockMultipartFile avatarFile = new MockMultipartFile(
                "avatar", "avatar.jpg", "image/jpeg", "fake image content".getBytes()
        );

        doNothing().when(userService).register(any());

        // Act & Assert
        mockMvc.perform(multipart("/api/auth/register")
                .file(avatarFile)
                .param("username", "testuser")
                .param("phone", "13800138000")
                .param("email", "test@example.com")
                .param("password", "test123")
                .param("gender", "男")
                .param("learningGoal", "职业提升")
                .param("learningPreference", "视觉学习", "听觉学习")
                .param("courseInterest", "编程开发", "前端学习")
                .param("emailCode", "123456")
                .param("userRole", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注册成功，请登录"));

        verify(userService, times(1)).register(any());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：缺少必填参数")
    void testRegister_MissingRequiredFields() throws Exception {
        // Arrange
        doThrow(new RuntimeException("用户名不能为空")).when(userService).register(any());

        // Act & Assert
        mockMvc.perform(multipart("/api/auth/register")
                .param("phone", "13800138000")
                .param("email", "test@example.com"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).register(any());
    }

    @Test
    @DisplayName("测试用户登录 - 正常场景：用户名登录")
    void testLogin_ValidUsername() throws Exception {
        // Arrange
        when(userService.login(any())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .param("usernameOrPhone", "testuser")
                .param("password", "test123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService, times(1)).login(any());
    }

    @Test
    @DisplayName("测试用户登录 - 异常情况：用户名或密码错误")
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        when(userService.login(any())).thenThrow(new RuntimeException("用户名或密码错误"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .param("usernameOrPhone", "testuser")
                .param("password", "wrongpassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("测试用户登录 - 异常情况：账号被冻结")
    void testLogin_FrozenAccount() throws Exception {
        // Arrange
        when(userService.login(any())).thenThrow(new RuntimeException("账号已被冻结，请联系管理员"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .param("usernameOrPhone", "testuser")
                .param("password", "test123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("账号已被冻结，请联系管理员"));
    }

    @Test
    @DisplayName("测试验证Token - 正常场景：有效Token")
    void testVerifyToken_ValidToken() throws Exception {
        // 注意：这里需要根据实际的token验证逻辑来mock
        // 由于AuthController使用内存存储token，需要实际设置token
        // 暂时跳过，需要实际运行应用来测试
        // TODO: 实现token验证测试
    }

    @Test
    @DisplayName("测试验证Token - 异常情况：无效Token")
    void testVerifyToken_InvalidToken() throws Exception {
        // Arrange
        String token = "invalid-token";

        // Act & Assert
        mockMvc.perform(get("/api/auth/verify")
                .param("token", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token无效或已过期"));
    }
}

