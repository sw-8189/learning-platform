package com.nchu.learningplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmailCodeService 单元测试
 * 测试邮箱验证码服务的各种功能
 */
@DisplayName("邮箱验证码服务测试")
class EmailCodeServiceTest {

    private EmailCodeService emailCodeService;

    @BeforeEach
    void setUp() {
        emailCodeService = new EmailCodeService();
    }

    @Test
    @DisplayName("测试生成验证码 - 正常场景：生成6位数字验证码")
    void testGenerateCode_ValidFormat() {
        // Act
        String code = emailCodeService.generateCode();

        // Assert
        assertNotNull(code);
        assertEquals(6, code.length(), "验证码应该是6位");
        assertTrue(code.matches("\\d{6}"), "验证码应该只包含数字");
    }

    @Test
    @DisplayName("测试生成验证码 - 多次生成应该不同")
    void testGenerateCode_UniqueCodes() {
        // Act
        String code1 = emailCodeService.generateCode();
        String code2 = emailCodeService.generateCode();

        // Assert
        // 虽然理论上可能相同，但概率极低
        assertNotEquals(code1, code2, "多次生成的验证码应该不同（概率极低相同）");
    }

    @Test
    @DisplayName("测试保存验证码 - 正常场景：首次保存")
    void testSaveCode_FirstTime() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";

        // Act
        boolean result = emailCodeService.saveCode(email, code);

        // Assert
        assertTrue(result, "首次保存应该成功");
    }

    @Test
    @DisplayName("测试保存验证码 - 异常情况：间隔时间太短")
    void testSaveCode_TooFrequent() throws InterruptedException {
        // Arrange
        String email = "test@example.com";
        String code1 = "123456";
        String code2 = "654321";

        // Act
        boolean result1 = emailCodeService.saveCode(email, code1);
        boolean result2 = emailCodeService.saveCode(email, code2);

        // Assert
        assertTrue(result1, "第一次保存应该成功");
        assertFalse(result2, "间隔时间太短应该失败");
    }

    @Test
    @DisplayName("测试验证验证码 - 正常场景：验证码正确")
    void testVerifyCode_CorrectCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.verifyCode(email, code);

        // Assert
        assertTrue(result, "正确的验证码应该验证通过");
    }

    @Test
    @DisplayName("测试验证验证码 - 异常情况：验证码错误")
    void testVerifyCode_WrongCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.verifyCode(email, "654321");

        // Assert
        assertFalse(result, "错误的验证码应该验证失败");
    }

    @Test
    @DisplayName("测试验证验证码 - 异常情况：邮箱不存在")
    void testVerifyCode_EmailNotExists() {
        // Arrange
        String email = "nonexistent@example.com";
        String code = "123456";

        // Act
        boolean result = emailCodeService.verifyCode(email, code);

        // Assert
        assertFalse(result, "不存在的邮箱应该验证失败");
    }

    @Test
    @DisplayName("测试检查验证码 - 正常场景：验证码正确且未过期")
    void testCheckCode_ValidCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.checkCode(email, code);

        // Assert
        assertTrue(result, "正确的验证码应该检查通过");
    }

    @Test
    @DisplayName("测试检查验证码 - 异常情况：验证码错误")
    void testCheckCode_WrongCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.checkCode(email, "654321");

        // Assert
        assertFalse(result, "错误的验证码应该检查失败");
    }

    @Test
    @DisplayName("测试删除验证码 - 正常场景")
    void testDeleteCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);
        assertTrue(emailCodeService.verifyCode(email, code), "删除前应该能验证");

        // Act
        emailCodeService.deleteCode(email);

        // Assert
        assertFalse(emailCodeService.verifyCode(email, code), "删除后应该不能验证");
    }

    @Test
    @DisplayName("测试清理过期验证码 - 正常场景")
    void testCleanExpiredCodes() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        emailCodeService.cleanExpiredCodes();

        // Assert
        // 由于验证码刚保存，应该还没过期，所以应该还能验证
        assertTrue(emailCodeService.verifyCode(email, code), "未过期的验证码应该还能验证");
    }

    @Test
    @DisplayName("测试是否可以发送验证码 - 正常场景：首次发送")
    void testCanSendCode_FirstTime() {
        // Arrange
        String email = "test@example.com";

        // Act
        boolean result = emailCodeService.canSendCode(email);

        // Assert
        assertTrue(result, "首次发送应该可以");
    }

    @Test
    @DisplayName("测试是否可以发送验证码 - 异常情况：间隔时间太短")
    void testCanSendCode_TooFrequent() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.canSendCode(email);

        // Assert
        assertFalse(result, "间隔时间太短应该不能发送");
    }

    @Test
    @DisplayName("测试获取剩余等待时间 - 正常场景：首次发送")
    void testGetRemainingWaitTime_FirstTime() {
        // Arrange
        String email = "test@example.com";

        // Act
        long remainingTime = emailCodeService.getRemainingWaitTime(email);

        // Assert
        assertEquals(0, remainingTime, "首次发送剩余时间应该为0");
    }

    @Test
    @DisplayName("测试获取剩余等待时间 - 正常场景：刚发送后")
    void testGetRemainingWaitTime_JustSent() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        long remainingTime = emailCodeService.getRemainingWaitTime(email);

        // Assert
        assertTrue(remainingTime > 0, "刚发送后应该有剩余等待时间");
        assertTrue(remainingTime <= 60, "剩余等待时间应该不超过60秒");
    }

    @Test
    @DisplayName("测试验证码过期 - 边界条件：验证码在有效期内")
    void testVerifyCode_WithinExpiryTime() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        emailCodeService.saveCode(email, code);

        // Act
        boolean result = emailCodeService.verifyCode(email, code);

        // Assert
        assertTrue(result, "有效期内应该能验证");
    }

    @Test
    @DisplayName("测试多个邮箱的验证码 - 正常场景：不同邮箱独立存储")
    void testMultipleEmails_IndependentStorage() {
        // Arrange
        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String code1 = "111111";
        String code2 = "222222";

        // Act
        emailCodeService.saveCode(email1, code1);
        emailCodeService.saveCode(email2, code2);

        // Assert
        assertTrue(emailCodeService.verifyCode(email1, code1), "邮箱1的验证码应该正确");
        assertTrue(emailCodeService.verifyCode(email2, code2), "邮箱2的验证码应该正确");
        assertFalse(emailCodeService.verifyCode(email1, code2), "邮箱1不应该验证邮箱2的验证码");
        assertFalse(emailCodeService.verifyCode(email2, code1), "邮箱2不应该验证邮箱1的验证码");
    }
}

