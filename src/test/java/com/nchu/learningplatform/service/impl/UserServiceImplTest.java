package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 * 测试用户服务的各种功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务实现类测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest validRegisterRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // 准备有效的注册请求
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("testuser");
        validRegisterRequest.setPhone("13800138000");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("test123");
        validRegisterRequest.setGender("男");
        validRegisterRequest.setLearningGoal("职业提升");
        validRegisterRequest.setLearningPreference(Arrays.asList("视觉学习", "听觉学习"));
        validRegisterRequest.setCourseInterest(Arrays.asList("编程开发", "前端学习"));

        // 准备已存在的用户
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPhone("13900139000");
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：所有参数有效")
    void testRegister_ValidRequest() {
        // Arrange
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.register(validRegisterRequest));

        // Assert
        verify(userMapper, times(1)).findByUsername("testuser");
        verify(userMapper, times(1)).findByPhone("13800138000");
        verify(userMapper, times(1)).findByEmail("test@example.com");
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：用户名为空")
    void testRegister_EmptyUsername() {
        // Arrange
        validRegisterRequest.setUsername("");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("用户名不能为空", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：用户名为null")
    void testRegister_NullUsername() {
        // Arrange
        validRegisterRequest.setUsername(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("用户名不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：手机号为空")
    void testRegister_EmptyPhone() {
        // Arrange
        validRegisterRequest.setPhone("");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("手机号不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：邮箱为空")
    void testRegister_EmptyEmail() {
        // Arrange
        validRegisterRequest.setEmail("");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("邮箱不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：密码为空")
    void testRegister_EmptyPassword() {
        // Arrange
        validRegisterRequest.setPassword("");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("密码不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：学习偏好为空")
    void testRegister_EmptyLearningPreference() {
        // Arrange
        validRegisterRequest.setLearningPreference(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("请至少选择一个学习偏好", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：课程兴趣为空")
    void testRegister_EmptyCourseInterest() {
        // Arrange
        validRegisterRequest.setCourseInterest(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("请至少选择一个课程兴趣", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：用户名已存在")
    void testRegister_UsernameExists() {
        // Arrange
        when(userMapper.findByUsername("testuser")).thenReturn(existingUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：手机号已被注册")
    void testRegister_PhoneExists() {
        // Arrange
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone("13800138000")).thenReturn(existingUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("手机号已被注册", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：邮箱已被注册")
    void testRegister_EmailExists() {
        // Arrange
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail("test@example.com")).thenReturn(existingUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：使用默认头像")
    void testRegister_DefaultAvatar() {
        // Arrange
        validRegisterRequest.setAvatarUrl(null);
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.register(validRegisterRequest));

        // Assert
        verify(userMapper).insert(argThat(user -> 
            "/image/avatar/default-avatar.png".equals(user.getAvatarUrl())
        ));
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：使用自定义头像")
    void testRegister_CustomAvatar() {
        // Arrange
        validRegisterRequest.setAvatarUrl("/uploads/avatar/custom.jpg");
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.register(validRegisterRequest));

        // Assert
        verify(userMapper).insert(argThat(user -> 
            "/uploads/avatar/custom.jpg".equals(user.getAvatarUrl())
        ));
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：默认角色为USER")
    void testRegister_DefaultRole() {
        // Arrange
        validRegisterRequest.setRole(null);
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.register(validRegisterRequest));

        // Assert
        verify(userMapper).insert(argThat(user -> 
            "USER".equals(user.getRole())
        ));
    }

    @Test
    @DisplayName("测试用户注册 - 正常场景：设置ADMIN角色")
    void testRegister_AdminRole() {
        // Arrange
        validRegisterRequest.setRole("ADMIN");
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.register(validRegisterRequest));

        // Assert
        verify(userMapper).insert(argThat(user -> 
            "ADMIN".equals(user.getRole())
        ));
    }

    @Test
    @DisplayName("测试用户注册 - 异常情况：并发冲突 - 用户名重复")
    void testRegister_DuplicateKeyException_Username() {
        // Arrange
        when(userMapper.findByUsername(anyString())).thenReturn(null);
        when(userMapper.findByPhone(anyString())).thenReturn(null);
        when(userMapper.findByEmail(anyString())).thenReturn(null);
        when(userMapper.insert(any(User.class)))
            .thenThrow(new DuplicateKeyException("Duplicate entry 'testuser' for key 'user.username'"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(validRegisterRequest);
        });
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户登录 - 正常场景：用户名登录")
    void testLogin_ValidUsername() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("testuser");
        request.setPassword("test123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("test123");
        user.setStatus("ACTIVE");

        when(userMapper.findByUsernameOrPhone("testuser")).thenReturn(user);

        // Act
        User result = userService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findByUsernameOrPhone("testuser");
    }

    @Test
    @DisplayName("测试用户登录 - 正常场景：手机号登录")
    void testLogin_ValidPhone() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("13800138000");
        request.setPassword("test123");

        User user = new User();
        user.setId(1L);
        user.setPhone("13800138000");
        user.setPassword("test123");
        user.setStatus("ACTIVE");

        when(userMapper.findByUsernameOrPhone("13800138000")).thenReturn(user);

        // Act
        User result = userService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
    }

    @Test
    @DisplayName("测试用户登录 - 异常情况：用户不存在")
    void testLogin_UserNotExists() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("nonexistent");
        request.setPassword("test123");

        when(userMapper.findByUsernameOrPhone("nonexistent")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户登录 - 异常情况：密码错误")
    void testLogin_WrongPassword() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("testuser");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("test123");
        user.setStatus("ACTIVE");

        when(userMapper.findByUsernameOrPhone("testuser")).thenReturn(user);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户登录 - 异常情况：账号被冻结")
    void testLogin_FrozenAccount() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsernameOrPhone("testuser");
        request.setPassword("test123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("test123");
        user.setStatus("FROZEN");

        when(userMapper.findByUsernameOrPhone("testuser")).thenReturn(user);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        assertEquals("账号已被冻结，请联系管理员", exception.getMessage());
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 正常场景")
    void testGetById_ValidId() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userMapper.findById(userId)).thenReturn(user);

        // Act
        User result = userService.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userMapper, times(1)).findById(userId);
    }

    @Test
    @DisplayName("测试根据ID获取用户 - 异常情况：用户不存在")
    void testGetById_UserNotExists() {
        // Arrange
        Long userId = 999L;
        when(userMapper.findById(userId)).thenReturn(null);

        // Act
        User result = userService.getById(userId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("测试更新用户信息 - 正常场景")
    void testUpdateUserInfo_ValidUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("newemail@example.com");
        when(userMapper.updateUserInfo(any(User.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.updateUserInfo(user));

        // Assert
        verify(userMapper, times(1)).updateUserInfo(user);
    }

    @Test
    @DisplayName("测试更新密码 - 正常场景")
    void testUpdatePassword_ValidPassword() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldpass123";
        String newPassword = "newpass123";

        User user = new User();
        user.setId(userId);
        user.setPassword(oldPassword);

        when(userMapper.findById(userId)).thenReturn(user);
        when(userMapper.updatePassword(userId, newPassword)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> {
            userService.updatePassword(userId, oldPassword, newPassword);
        });

        // Assert
        verify(userMapper, times(1)).findById(userId);
        verify(userMapper, times(1)).updatePassword(userId, newPassword);
    }

    @Test
    @DisplayName("测试更新密码 - 异常情况：用户不存在")
    void testUpdatePassword_UserNotExists() {
        // Arrange
        Long userId = 999L;
        String oldPassword = "oldpass123";
        String newPassword = "newpass123";

        when(userMapper.findById(userId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(userId, oldPassword, newPassword);
        });
        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    @DisplayName("测试更新密码 - 异常情况：原密码错误")
    void testUpdatePassword_WrongOldPassword() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "wrongpass";
        String newPassword = "newpass123";

        User user = new User();
        user.setId(userId);
        user.setPassword("correctpass");

        when(userMapper.findById(userId)).thenReturn(user);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(userId, oldPassword, newPassword);
        });
        assertEquals("原密码错误", exception.getMessage());
        verify(userMapper, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    @DisplayName("测试邮箱是否存在 - 正常场景：邮箱存在")
    void testExistsByEmail_EmailExists() {
        // Arrange
        String email = "test@example.com";
        when(userMapper.findByEmail(email)).thenReturn(existingUser);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userMapper, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("测试邮箱是否存在 - 正常场景：邮箱不存在")
    void testExistsByEmail_EmailNotExists() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userMapper.findByEmail(email)).thenReturn(null);

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userMapper, times(1)).findByEmail(email);
    }
}

