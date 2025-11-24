// learning-platform/src/main/java/com/nchu/learningplatform/controller/AuthController.java
package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.LoginRequest;
import com.nchu.learningplatform.dto.RegisterRequest;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private UserService userService;

    /** 非生产用的简单内存 token 存储 */
    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(Map.of("message", "注册成功，请登录"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "role", user.getRole(),
                "username", user.getUsername()
        ));
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
