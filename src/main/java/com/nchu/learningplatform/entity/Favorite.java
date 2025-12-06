package com.nchu.learningplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createTime;
}

