package com.nchu.learningplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    
    // 关联信息（用于前端显示）
    private String senderUsername;
    private String senderAvatarUrl;
    private String receiverUsername;
    private String receiverAvatarUrl;
}

