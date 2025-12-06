package com.nchu.learningplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private String type; // like, comment, reply, follow, best_answer
    private Long relatedId;
    private Long fromUserId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    
    // 关联信息
    private String fromUsername;
    private String fromAvatarUrl;
}

