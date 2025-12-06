package com.nchu.learningplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Boolean isBestAnswer;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息
    private String username;
    private String avatarUrl;
    private Boolean isLiked; // 当前用户是否已点赞
    private List<Comment> replies; // 子评论列表
}

