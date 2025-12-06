package com.example.service;

import cn.hutool.core.util.RandomUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 功能
 * 作者：封延民
 * 日期：2025/2/25 16:27
 */
@Service
public class EmailService {
    private Integer code;
    @Value("${spring.mail.username}")
    private String from="";
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private TemplateEngine templateEngine;
    public String sendCode(String to, String subject){
        code = RandomUtil.randomInt(100000, 999999);

        // 准备模板上下文
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("subject", subject);
        // 渲染模板
        String htmlContent = templateEngine.process("email-code", context);

        // 创建邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR);
        }

        javaMailSender.send(mimeMessage);
        return code.toString();
    }

    public void sendBackPassword(String email, String username, String newPassword) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("newPassword", newPassword);

        String htmlContent = templateEngine.process("email-password", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("校园帮-密码重置");
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR);
        }

        javaMailSender.send(mimeMessage);
    }
}
