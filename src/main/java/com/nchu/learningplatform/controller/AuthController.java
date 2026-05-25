package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.dto.ResetPasswordRequest;
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

    @Value("${app.admin.invite-code:TeamWorkClass-2026}")
    private String adminInviteCode;

    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email,
                                      @RequestParam(defaultValue = "register") String type) {
        try {
            if (email == null || email.trim().isEmpty() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid email format"));
            }

            if (!emailCodeService.canSendCode(email)) {
                long remainingTime = emailCodeService.getRemainingWaitTime(email);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "Please try again later. You need to wait " + remainingTime + " seconds since the last request",
                                "remainingTime", remainingTime));
            }

            if ("reset".equals(type)) {
                if (!userService.existsByEmail(email.trim())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "This email is not registered"));
                }
            } else {
                if (userService.existsByEmail(email.trim())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "This email is already registered"));
                }
            }

            String code = emailCodeService.generateCode();

            if (!emailCodeService.saveCode(email.trim(), code)) {
                long remainingTime = emailCodeService.getRemainingWaitTime(email);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "Please try again later. You need to wait " + remainingTime + " seconds since the last request",
                                "remainingTime", remainingTime));
            }

            boolean isReset = "reset".equals(type);
            String title = isReset
                    ? "Smart Learning Platform - Password Reset Verification Code"
                    : "Smart Learning Platform - Registration Verification Code";
            String purpose = isReset
                    ? "You are resetting your Smart Learning Platform account password. Your verification code is:"
                    : "You are registering a Smart Learning Platform account. Your verification code is:";
            String content = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2 style='color: #3182ce;'>Smart Learning Platform</h2>" +
                    "<p>Dear user, hello!</p>" +
                    "<p>" + purpose + "</p>" +
                    "<div style='background-color: #f0f8ff; padding: 15px; margin: 20px 0; text-align: center;'>" +
                    "<h1 style='color: #3182ce; font-size: 32px; margin: 0; letter-spacing: 5px;'>" + code + "</h1>" +
                    "</div>" +
                    "<p style='color: #666;'>The verification code is valid for 5 minutes. Do not share it with others.</p>" +
                    "<p style='color: #666;'>If you did not make this request, please ignore this email.</p>" +
                    "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<p style='color: #999; font-size: 12px;'>This email was sent automatically by the system. Please do not reply.</p>" +
                    "</div>";

            boolean sendSuccess = mailUtils.sendMail(email.trim(), content, title);
            if (sendSuccess) {
                return ResponseEntity.ok(Map.of("message", "Verification code has been sent to your email. Please check your inbox"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Failed to send verification code. Please try again later"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send verification code. Please try again later"));
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
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username is required"));
            }
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Phone number is required"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
            }
            if (gender == null || gender.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Gender is required"));
            }
            if (learningGoal == null || learningGoal.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Learning goal is required"));
            }
            if (learningPreference == null || learningPreference.length == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Please select at least one learning preference"));
            }
            if (courseInterest == null || courseInterest.length == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Please select at least one course interest"));
            }
            if (emailCode == null || emailCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Verification code is required"));
            }

            String trimmedEmail = email.trim();
            if (!trimmedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid email format"));
            }

            String trimmedPhone = phone.trim();
            if (!trimmedPhone.matches("^1[3-9]\\d{9}$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid phone number format. Please enter a valid 11-digit phone number"));
            }

            if (!emailCodeService.verifyCode(trimmedEmail, emailCode.trim())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired verification code. Please request a new one"));
            }

            String avatarUrl = null;
            if (avatar != null && !avatar.isEmpty()) {
                long maxSize = 2 * 1024 * 1024;
                if (avatar.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Avatar file size cannot exceed 2MB"));
                }

                String originalFilename = avatar.getOriginalFilename();
                if (originalFilename != null) {
                    String lowerFilename = originalFilename.toLowerCase();
                    boolean isValidType = lowerFilename.endsWith(".jpg") ||
                                        lowerFilename.endsWith(".jpeg") ||
                                        lowerFilename.endsWith(".png") ||
                                        lowerFilename.endsWith(".gif");
                    if (!isValidType) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Unsupported avatar file format. Only JPG, JPEG, PNG, GIF are supported"));
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
                        throw new RuntimeException("Unable to create target file");
                    }
                    avatar.transferTo(targetFile);
                    avatarUrl = "/uploads/avatar/" + filename;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Avatar upload failed. Please try again later"));
                }
            }

            String role = "USER";
            if (userRole != null && !userRole.trim().isEmpty()) {
                String trimmedRole = userRole.trim().toUpperCase();
                if ("ADMIN".equals(trimmedRole)) {
                    if (adminInviteCode == null || adminInviteCode.trim().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "The system is not configured with an admin invite code. Direct admin registration is currently not supported"));
                    }
                    if (adminInviteCodeParam == null || adminInviteCodeParam.trim().isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Admin invite code is required when selecting admin role"));
                    }
                    if (!adminInviteCode.trim().equals(adminInviteCodeParam.trim())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "Invalid admin invite code. Please contact the platform administrator for the correct code"));
                    }
                    role = "ADMIN";
                }
            }

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

            userService.register(request);

            try {
                emailCodeService.deleteCode(trimmedEmail);
            } catch (Exception e) {
                System.err.println("Failed to delete verification code, but registration was successful: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of("message", "Registration successful. Please log in"));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();

            Throwable cause = e.getCause();
            if (cause != null) {
                String causeMessage = cause.getMessage();
                if (causeMessage != null) {
                    if (causeMessage.contains("Duplicate entry") && causeMessage.contains("username")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Username is already taken. Please choose a different one"));
                    } else if (causeMessage.contains("Duplicate entry") && causeMessage.contains("phone")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Phone number is already registered. Please use a different one"));
                    } else if (causeMessage.contains("Duplicate entry") && causeMessage.contains("email")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Email is already registered. Please use a different one"));
                    }
                }
            }

            String fullExceptionMessage = e.toString() + (e.getMessage() != null ? " " + e.getMessage() : "");
            if (fullExceptionMessage.contains("DuplicateKeyException") ||
                fullExceptionMessage.contains("Duplicate entry")) {
                String exceptionMessage = fullExceptionMessage.toLowerCase();
                if (exceptionMessage.contains("username") || exceptionMessage.contains("'user.username'")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Username is already taken. Please choose a different one"));
                } else if (exceptionMessage.contains("phone") || exceptionMessage.contains("'user.phone'")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Phone number is already registered. Please use a different one"));
                } else if (exceptionMessage.contains("email") || exceptionMessage.contains("'user.email'")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Email is already registered. Please use a different one"));
                }
            }

            if (errorMessage != null) {
                if (errorMessage.contains("Username already exists") || errorMessage.contains("用户名已存在")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Username is already taken. Please choose a different one"));
                } else if (errorMessage.contains("Phone already registered") || errorMessage.contains("手机号已被注册")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Phone number is already registered. Please use a different one"));
                } else if (errorMessage.contains("Email already registered") || errorMessage.contains("邮箱已被注册")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Email is already registered. Please use a different one"));
                } else if (errorMessage.contains("Username is required") || errorMessage.contains("用户名不能为空")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Username is required"));
                } else if (errorMessage.contains("is required") || errorMessage.contains("不能为空")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", errorMessage));
                } else if (errorMessage.contains("select at least") || errorMessage.contains("至少选择")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", errorMessage));
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed. Please check your information and try again. If the problem persists, please contact support."));
        }
    }

    @PostMapping("/check-code")
    public ResponseEntity<?> checkCode(@RequestParam String email,
                                       @RequestParam String emailCode) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        if (emailCode == null || emailCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Verification code is required"));
        }
        boolean ok = emailCodeService.checkCode(email.trim(), emailCode.trim());
        if (ok) {
            return ResponseEntity.ok(Map.of("message", "Verification code is correct"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired verification code. Please check and try again"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            String email = request.getEmail().trim();
            String code = request.getCode().trim();
            String newPassword = request.getNewPassword();

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid email format"));
            }

            if (!userService.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("message", "This email is not registered"));
            }

            if (!emailCodeService.verifyCode(email, code)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired verification code"));
            }

            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
            }
            if (!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must contain both letters and numbers"));
            }

            userService.resetPassword(email, newPassword);
            emailCodeService.deleteCode(email);

            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully. Please log in with your new password"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reset password. Please try again later"));
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
            String msg = e.getMessage() != null ? e.getMessage() : "Login failed. Please try again later";
            if (msg.contains("Account has been frozen") || msg.contains("账号已被冻结")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message", "Your account has been frozen and cannot log in. If you have questions, please contact the platform administrator or support."
                        ));
            }
            if (msg.contains("Invalid username or password") || msg.contains("用户名或密码错误")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "Invalid username or password. Please check and try again."
                        ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Login failed. Please try again later. If the problem persists, please contact the platform administrator."
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String token) {
        Long userId = tokenStore.get(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    public Long getUserIdByToken(String token) {
        return tokenStore.get(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        tokenStore.remove(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
