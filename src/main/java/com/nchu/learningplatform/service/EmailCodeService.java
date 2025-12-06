package com.nchu.learningplatform.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮箱验证码服务
 * 存储邮箱和验证码的映射关系，带过期时间
 */
@Service
public class EmailCodeService {

    // 存储邮箱和验证码的映射，格式：email -> {code, expireTime}
    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();
    
    // 验证码有效期（5分钟）
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;
    
    // 发送验证码的间隔时间（60秒）
    private static final long SEND_INTERVAL = 60 * 1000;
    
    // 存储上次发送时间，格式：email -> lastSendTime
    private final Map<String, Long> lastSendTime = new ConcurrentHashMap<>();

    /**
     * 验证码信息
     */
    private static class CodeInfo {
        String code;
        long expireTime;
        
        CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 生成6位数字验证码
     */
    public String generateCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    /**
     * 保存验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否保存成功（如果距离上次发送时间太短，返回false）
     */
    public boolean saveCode(String email, String code) {
        // 检查发送间隔
        Long lastSend = lastSendTime.get(email);
        if (lastSend != null && System.currentTimeMillis() - lastSend < SEND_INTERVAL) {
            return false;
        }
        
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_TIME;
        codeStore.put(email, new CodeInfo(code, expireTime));
        lastSendTime.put(email, System.currentTimeMillis());
        return true;
    }

    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = codeStore.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        if (codeInfo.isExpired()) {
            codeStore.remove(email);
            return false;
        }
        
        // 只检查是否匹配，不在这里删除验证码，
        // 这样用户在修正其他表单错误时可以重复提交，不会被验证码阻塞
        return codeInfo.code.equals(code);
    }
    
    /**
     * 删除验证码（注册成功后调用，避免重复使用）
     * @param email 邮箱
     */
    public void deleteCode(String email) {
        codeStore.remove(email);
    }
    
    /**
     * 仅检查验证码是否正确且未过期（不删除）
     */
    public boolean checkCode(String email, String code) {
        CodeInfo codeInfo = codeStore.get(email);
        if (codeInfo == null || codeInfo.isExpired()) {
            return false;
        }
        return codeInfo.code.equals(code);
    }

    /**
     * 清理过期的验证码
     */
    public void cleanExpiredCodes() {
        codeStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 检查是否可以发送验证码（距离上次发送是否超过间隔时间）
     * @param email 邮箱
     * @return 是否可以发送
     */
    public boolean canSendCode(String email) {
        Long lastSend = lastSendTime.get(email);
        if (lastSend == null) {
            return true;
        }
        return System.currentTimeMillis() - lastSend >= SEND_INTERVAL;
    }

    /**
     * 获取剩余等待时间（秒）
     * @param email 邮箱
     * @return 剩余等待时间（秒），如果可以直接发送则返回0
     */
    public long getRemainingWaitTime(String email) {
        Long lastSend = lastSendTime.get(email);
        if (lastSend == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - lastSend;
        if (elapsed >= SEND_INTERVAL) {
            return 0;
        }
        return (SEND_INTERVAL - elapsed) / 1000;
    }
}

