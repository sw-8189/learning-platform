package com.nchu.learningplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCourse {

    private Long id;

    private Long userId;

    private Long courseId;

    private LocalDateTime joinTime;
}
