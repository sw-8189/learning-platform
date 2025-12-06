package com.nchu.learningplatform.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PasswordUtils 单元测试
 * 测试密码工具类的各种功能
 */
@DisplayName("密码工具类测试")
class PasswordUtilsTest {

    @Test
    @DisplayName("测试密码强度验证 - 正常场景：包含字母和数字的6位以上密码")
    void testIsPasswordStrong_ValidPassword() {
        // Arrange
        String password = "abc123";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertTrue(result, "包含字母和数字的6位密码应该通过强度验证");
    }

    @Test
    @DisplayName("测试密码强度验证 - 边界条件：正好6位")
    void testIsPasswordStrong_Exactly6Chars() {
        // Arrange
        String password = "abc123";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("测试密码强度验证 - 边界条件：超过6位")
    void testIsPasswordStrong_MoreThan6Chars() {
        // Arrange
        String password = "password123";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("测试密码强度验证 - 异常情况：null密码")
    void testIsPasswordStrong_NullPassword() {
        // Arrange
        String password = null;

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertFalse(result, "null密码应该返回false");
    }

    @Test
    @DisplayName("测试密码强度验证 - 异常情况：空字符串")
    void testIsPasswordStrong_EmptyPassword() {
        // Arrange
        String password = "";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertFalse(result, "空字符串应该返回false");
    }

    @Test
    @DisplayName("测试密码强度验证 - 边界条件：少于6位")
    void testIsPasswordStrong_LessThan6Chars() {
        // Arrange
        String password = "abc12";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertFalse(result, "少于6位的密码应该返回false");
    }

    @Test
    @DisplayName("测试密码强度验证 - 异常情况：只有字母")
    void testIsPasswordStrong_OnlyLetters() {
        // Arrange
        String password = "abcdef";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertFalse(result, "只有字母的密码应该返回false");
    }

    @Test
    @DisplayName("测试密码强度验证 - 异常情况：只有数字")
    void testIsPasswordStrong_OnlyNumbers() {
        // Arrange
        String password = "123456";

        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertFalse(result, "只有数字的密码应该返回false");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "ABC123", "a1b2c3", "123abc", "AbC123"})
    @DisplayName("测试密码强度验证 - 参数化测试：各种有效密码格式")
    void testIsPasswordStrong_ValidFormats(String password) {
        // Act
        boolean result = PasswordUtils.isPasswordStrong(password);

        // Assert
        assertTrue(result, "密码 " + password + " 应该通过验证");
    }

    @Test
    @DisplayName("测试密码强度提示 - 正常场景：有效密码")
    void testGetPasswordStrengthHint_ValidPassword() {
        // Arrange
        String password = "abc123";

        // Act
        String hint = PasswordUtils.getPasswordStrengthHint(password);

        // Assert
        assertEquals("密码强度良好", hint);
    }

    @Test
    @DisplayName("测试密码强度提示 - 异常情况：null密码")
    void testGetPasswordStrengthHint_NullPassword() {
        // Arrange
        String password = null;

        // Act
        String hint = PasswordUtils.getPasswordStrengthHint(password);

        // Assert
        assertEquals("请输入密码", hint);
    }

    @Test
    @DisplayName("测试密码强度提示 - 异常情况：空字符串")
    void testGetPasswordStrengthHint_EmptyPassword() {
        // Arrange
        String password = "";

        // Act
        String hint = PasswordUtils.getPasswordStrengthHint(password);

        // Assert
        assertEquals("请输入密码", hint);
    }

    @Test
    @DisplayName("测试密码强度提示 - 边界条件：少于6位")
    void testGetPasswordStrengthHint_LessThan6Chars() {
        // Arrange
        String password = "abc12";

        // Act
        String hint = PasswordUtils.getPasswordStrengthHint(password);

        // Assert
        assertEquals("密码长度至少6位", hint);
    }

    @Test
    @DisplayName("测试密码强度提示 - 异常情况：缺少字母或数字")
    void testGetPasswordStrengthHint_MissingLetterOrDigit() {
        // Arrange
        String password = "abcdef";

        // Act
        String hint = PasswordUtils.getPasswordStrengthHint(password);

        // Assert
        assertEquals("密码应包含字母和数字", hint);
    }

    @Test
    @DisplayName("测试密码哈希 - 正常场景")
    void testHashPassword() {
        // Arrange
        String plainPassword = "test123";

        // Act
        String hashed = PasswordUtils.hashPassword(plainPassword);

        // Assert
        assertNotNull(hashed);
        // 注意：当前实现返回明文，生产环境应该加密
        assertEquals(plainPassword, hashed, "当前实现返回明文（仅用于演示）");
    }

    @Test
    @DisplayName("测试密码匹配 - 正常场景：匹配")
    void testMatches_ValidMatch() {
        // Arrange
        String plainPassword = "test123";
        String storedPassword = "test123";

        // Act
        boolean result = PasswordUtils.matches(plainPassword, storedPassword);

        // Assert
        assertTrue(result, "相同密码应该匹配");
    }

    @Test
    @DisplayName("测试密码匹配 - 异常情况：不匹配")
    void testMatches_InvalidMatch() {
        // Arrange
        String plainPassword = "test123";
        String storedPassword = "test456";

        // Act
        boolean result = PasswordUtils.matches(plainPassword, storedPassword);

        // Assert
        assertFalse(result, "不同密码不应该匹配");
    }

    @Test
    @DisplayName("测试密码匹配 - 异常情况：null密码")
    void testMatches_NullPassword() {
        // Arrange
        String plainPassword = null;
        String storedPassword = "test123";

        // Act
        boolean result = PasswordUtils.matches(plainPassword, storedPassword);

        // Assert
        assertFalse(result, "null密码不应该匹配");
    }

    @Test
    @DisplayName("测试密码匹配 - 异常情况：null存储密码")
    void testMatches_NullStoredPassword() {
        // Arrange
        String plainPassword = "test123";
        String storedPassword = null;

        // Act
        boolean result = PasswordUtils.matches(plainPassword, storedPassword);

        // Assert
        assertFalse(result, "null存储密码不应该匹配");
    }
}

