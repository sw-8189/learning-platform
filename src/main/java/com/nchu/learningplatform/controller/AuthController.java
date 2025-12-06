// learning-platform/src/main/java/com/nchu/learningplatform/controller/AuthController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.service.EmailCodeService;
import com.nchu.learningplatform.service.UserService;
import com.nchu.learningplatform.util.MailUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private MailUtils mailUtils;

    @Value("${app.upload.avatar-dir:uploads/avatar}")
    private String avatarUploadDir;

    /**
     * 管理员注册邀请码/后台口令
     * 通过 application.yml 中 app.admin.invite-code 配置
     */
    @Value("${app.admin.invite-code:}")
    private String adminInviteCode;

    /** 非生产用的简单内存 token 存储 */
    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        try {
            // 验证邮箱格式
            if (email == null || email.trim().isEmpty() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "邮箱格式不正确"));
            }

            // 检查是否可以发送（防止频繁发送）
            if (!emailCodeService.canSendCode(email)) {
                long remainingTime = emailCodeService.getRemainingWaitTime(email);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "请稍后再试，距离上次发送还需等待 " + remainingTime + " 秒", 
                                "remainingTime", remainingTime));
            }

            // 检查邮箱是否已被注册
            if (userService.existsByEmail(email.trim())) {
                return ResponseEntity.badRequest().body(Map.of("message", "该邮箱已被注册"));
            }

            // 生成验证码
            String code = emailCodeService.generateCode();

            // 保存验证码
            if (!emailCodeService.saveCode(email.trim(), code)) {
                long remainingTime = emailCodeService.getRemainingWaitTime(email);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "请稍后再试，距离上次发送还需等待 " + remainingTime + " 秒",
                                "remainingTime", remainingTime));
            }

            // 发送邮件
            String title = "智慧学习平台 - 注册验证码";
            String content = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2 style='color: #3182ce;'>智慧学习平台</h2>" +
                    "<p>尊敬的用户，您好！</p>" +
                    "<p>您正在注册智慧学习平台账号，验证码为：</p>" +
                    "<div style='background-color: #f0f8ff; padding: 15px; margin: 20px 0; text-align: center;'>" +
                    "<h1 style='color: #3182ce; font-size: 32px; margin: 0; letter-spacing: 5px;'>" + code + "</h1>" +
                    "</div>" +
                    "<p style='color: #666;'>验证码有效期为 5 分钟，请勿泄露给他人。</p>" +
                    "<p style='color: #666;'>如非本人操作，请忽略此邮件。</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<p style='color: #999; font-size: 12px;'>此邮件由系统自动发送，请勿回复。</p>" +
                    "</div>";

            boolean sendSuccess = mailUtils.sendMail(email.trim(), content, title);
            if (sendSuccess) {
                return ResponseEntity.ok(Map.of("message", "验证码已发送到您的邮箱，请查收"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "验证码发送失败，请稍后重试"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "发送验证码失败，请稍后重试"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String[] learningPreference,
            @RequestParam(required = false) String[] courseInterest,
            @RequestParam(required = false) String learningGoal,
            @RequestParam(required = false) String emailCode,
            @RequestParam(required = false) String userRole,
            @RequestParam(required = false, name = "adminInviteCode") String adminInviteCodeParam) {
        try {
            // 先进行基本参数验证
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "用户名不能为空"));
            }
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "手机号不能为空"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "邮箱不能为空"));
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "密码不能为空"));
            }
            if (gender == null || gender.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "性别不能为空"));
            }
            if (learningGoal == null || learningGoal.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "学习目标不能为空"));
            }
            if (learningPreference == null || learningPreference.length == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "请至少选择一个学习偏好"));
            }
            if (courseInterest == null || courseInterest.length == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "请至少选择一个课程兴趣"));
            }
            if (emailCode == null || emailCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "验证码不能为空"));
            }
            
            // 验证邮箱格式
            String trimmedEmail = email.trim();
            if (!trimmedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "邮箱格式不正确"));
            }
            
            // 验证手机号格式
            String trimmedPhone = phone.trim();
            if (!trimmedPhone.matches("^1[3-9]\\d{9}$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "手机号格式不正确，请输入11位有效手机号"));
            }
            
            // 验证验证码（在所有基本参数验证通过后）
            if (!emailCodeService.verifyCode(trimmedEmail, emailCode.trim())) {
                return ResponseEntity.badRequest().body(Map.of("message", "验证码错误或已过期，请重新获取"));
            }
            
            // 处理头像上传（在验证通过后）
            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                // 验证文件大小（限制 2MB）
                long maxSize = 2 * 1024 * 1024; // 2MB
                if (avatar.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body(Map.of("message", "头像文件大小不能超过2MB"));
                }
                
                // 验证文件类型
                String originalFilename = avatar.getOriginalFilename();
                if (originalFilename != null) {
                    String lowerFilename = originalFilename.toLowerCase();
                    boolean isValidType = lowerFilename.endsWith(".jpg") || 
                                        lowerFilename.endsWith(".jpeg") || 
                                        lowerFilename.endsWith(".png") || 
                                        lowerFilename.endsWith(".gif");
                    if (!isValidType) {
                        return ResponseEntity.badRequest().body(Map.of("message", "头像文件格式不支持，仅支持 JPG、JPEG、PNG、GIF 格式"));
                    }
                }
                
                try {
                    java.nio.file.Path dir = java.nio.file.Paths.get(avatarUploadDir);
                    if (!dir.isAbsolute()) {
                        dir = java.nio.file.Paths.get(System.getProperty("user.dir")).resolve(avatarUploadDir);
                    }
                    java.nio.file.Files.createDirectories(dir);

                    String ext = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
                    }
                    String filename = java.util.UUID.randomUUID().toString() + ext;
                    java.nio.file.Path target = dir.resolve(filename);

                    java.io.File targetFile = target.toFile();
                    if (targetFile == null) {
                        throw new RuntimeException("无法创建目标文件");
                    }
                    avatar.transferTo(targetFile);
                    // 注意：文件保存在 uploads/avatar/ 目录，静态资源映射 /uploads/** 到该目录
                    // 所以访问路径应该是 /uploads/avatar/filename
                    avatarUrl = "/uploads/avatar/" + filename;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "头像上传失败，请稍后重试"));
                }
            }

            // 验证和设置角色（默认USER，如果选择ADMIN需要邀请码验证）
            String role = "USER"; // 默认角色
            if (userRole != null && !userRole.trim().isEmpty()) {
                String trimmedRole = userRole.trim().toUpperCase();
                if ("ADMIN".equals(trimmedRole)) {
                    // 管理员邀请码未配置
                    if (adminInviteCode == null || adminInviteCode.trim().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "当前系统未配置管理员邀请码，暂不支持直接注册管理员账号"));
                    }
                    // 未填写邀请码
                    if (adminInviteCodeParam == null || adminInviteCodeParam.trim().isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "请选择管理员身份时必须填写管理员邀请码"));
                    }
                    // 校验邀请码
                    if (!adminInviteCode.trim().equals(adminInviteCodeParam.trim())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "管理员邀请码不正确，请联系平台管理员获取正确口令"));
                    }
                    role = "ADMIN";
                }
            }
            
            // 构建注册请求
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username.trim());
            request.setPhone(trimmedPhone);
            request.setEmail(trimmedEmail);
            request.setPassword(password);
            request.setGender(gender.trim());
            request.setLearningPreference(java.util.Arrays.asList(learningPreference));
            request.setCourseInterest(java.util.Arrays.asList(courseInterest));
            request.setLearningGoal(learningGoal.trim());
            request.setAvatarUrl(avatarUrl);
            request.setRole(role);

            // 执行注册
            userService.register(request);
            
            // 注册成功后删除验证码，避免重复使用
            emailCodeService.deleteCode(trimmedEmail);
            
            return ResponseEntity.ok(Map.of("message", "注册成功，请登录"));
        } catch (Exception e) {
            e.printStackTrace(); // 打印错误堆栈以便调试
            String errorMessage = e.getMessage();
            // 提供更友好的错误提示
            if (errorMessage != null) {
                if (errorMessage.contains("用户名已存在")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "用户名已被注册，请更换其他用户名"));
                } else if (errorMessage.contains("手机号已被注册")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "手机号已被注册，请使用其他手机号"));
                } else if (errorMessage.contains("邮箱已被注册")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "邮箱已被注册，请使用其他邮箱"));
                } else if (errorMessage.contains("用户名不能为空")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "用户名不能为空"));
                } else if (errorMessage.contains("不能为空")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", errorMessage));
                } else if (errorMessage.contains("至少选择")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", errorMessage));
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "注册失败，请检查信息后重试。如问题持续，请联系客服。"));
        }
    }

    /**
     * 前端即时校验邮箱验证码是否正确（不消耗验证码）
     */
    @PostMapping("/check-code")
    public ResponseEntity<?> checkCode(@RequestParam String email,
                                       @RequestParam String emailCode) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "邮箱不能为空"));
        }
        if (emailCode == null || emailCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "验证码不能为空"));
        }
        boolean ok = emailCodeService.checkCode(email.trim(), emailCode.trim());
        if (ok) {
            return ResponseEntity.ok(Map.of("message", "验证码正确"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "验证码错误或已过期，请检查后重试"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            String token = UUID.randomUUID().toString();
            tokenStore.put(token, user.getId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", user.getId(),
                    "role", user.getRole(),
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "登录失败，请稍后重试";
            // 账号被冻结：返回 403，并给出友好提示
            if (msg.contains("账号已被冻结")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message", "账号已被冻结，暂时无法登录。如有疑问，请联系平台管理员或客服处理。"
                        ));
            }
            // 用户名或密码错误：返回 401
            if (msg.contains("用户名或密码错误")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "用户名或密码错误，请检查后重试。"
                        ));
            }
            // 其他情况：统一为服务器错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "登录失败，请稍后重试。如问题持续，请联系平台管理员。"
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String token) {
        Long userId = tokenStore.get(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    /** 供其他 Controller 使用 */
    public Long getUserIdByToken(String token) {
        return tokenStore.get(token);
    }
}
