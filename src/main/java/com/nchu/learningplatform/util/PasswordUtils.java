package com.nchu.learningplatform.util;

/**
 * 密码工具类
 * 
 * 注意：当前版本使用明文存储密码，仅用于学习和演示目的。
 * 在生产环境中，必须使用密码加密（如BCrypt）！
 * 
 * TODO: 在生产环境中实现密码加密
 * 建议使用 Spring Security 的 BCryptPasswordEncoder 进行密码加密
 * 
 * 示例代码（待实现）：
 * <pre>
 * @Bean
 * public PasswordEncoder passwordEncoder() {
 *     return new BCryptPasswordEncoder();
 * }
 * 
 * // 注册时加密密码
 * String encodedPassword = passwordEncoder.encode(rawPassword);
 * 
 * // 登录时验证密码
 * boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
 * </pre>
 */
public class PasswordUtils {
    
    /**
     * 验证密码强度
     * @param password 密码
     * @return 是否满足强度要求
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // 至少包含字母和数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        return hasLetter && hasDigit;
    }

    /**
     * 获取密码强度提示
     * @param password 密码
     * @return 强度提示信息
     */
    public static String getPasswordStrengthHint(String password) {
        if (password == null || password.isEmpty()) {
            return "请输入密码";
        }
        if (password.length() < 6) {
            return "密码长度至少6位";
        }
        if (!isPasswordStrong(password)) {
            return "密码应包含字母和数字";
        }
        return "密码强度良好";
    }
    
    /**
     * 警告：当前使用明文密码，仅用于演示！
     * 在生产环境中必须加密存储密码。
     */
    public static String hashPassword(String plainPassword) {
        // TODO: 实现密码加密
        // 当前仅返回明文，生产环境必须加密！
        return plainPassword;
    }
    
    /**
     * 警告：当前使用明文密码比较，仅用于演示！
     * 在生产环境中必须使用加密后的密码进行比较。
     */
    public static boolean matches(String plainPassword, String storedPassword) {
        // TODO: 实现加密密码比较
        // 当前仅进行明文比较，生产环境必须使用加密比较！
        return plainPassword != null && plainPassword.equals(storedPassword);
    }
}

