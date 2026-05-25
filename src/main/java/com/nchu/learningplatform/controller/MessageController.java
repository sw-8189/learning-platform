package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.entity.Message;
import com.nchu.learningplatform.service.MessageService;
import com.nchu.learningplatform.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private AdminService adminService;

    @Resource
    private AuthController authController;

    /**
     * 验证用户权限
     */
    private Long getUserIdByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return authController.getUserIdByToken(token);
    }

    /**
     * 发送私信
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> request) {
        Long senderId = getUserIdByToken(token);
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }

        try {
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            String content = request.get("content").toString();
            
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Message content cannot be empty"));
            }

            Message message = messageService.sendMessage(senderId, receiverId, content);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send message: " + e.getMessage()));
        }
    }

    /**
     * 获取与某个用户的会话消息
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> getConversation(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long otherUserId) {
        Long userId = getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }

        try {
            List<Message> messages = messageService.getConversation(userId, otherUserId);
            // 标记消息为已读
            messageService.markAsRead(userId, otherUserId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch conversation: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }

        try {
            List<Message> conversations = messageService.getConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch conversation list: " + e.getMessage()));
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }

        try {
            int count = messageService.getUnreadCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch unread message count: " + e.getMessage()));
        }
    }
    
    /**
     * 标记单条消息为已读
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in or session expired"));
        }

        try {
            messageService.markMessageAsRead(messageId, userId);
            return ResponseEntity.ok(Map.of("message", "Message marked as read"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to mark message: " + e.getMessage()));
        }
    }
}

