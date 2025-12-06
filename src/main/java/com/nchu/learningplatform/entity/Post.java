package com.nchu.learningplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type; // discussion, question, experience
    private String category;
    private Long courseId;
    private String tags;
    private String attachmentUrl;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private String status;
    private String visibility; // PUBLIC(公开), PRIVATE(私密)
    private Boolean isTop;
    private Boolean isResolved;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息（不存储在数据库）
    private String username;
    private String avatarUrl;
    private String courseTitle;
    private Boolean isLiked; // 当前用户是否已点赞
    private Boolean isFavorited; // 当前用户是否已收藏
}

