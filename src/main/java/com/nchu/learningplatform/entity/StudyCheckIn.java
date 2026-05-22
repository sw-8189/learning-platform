package com.nchu.learningplatform.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudyCheckIn {

    private Long id;

    private Long userId;

    private LocalDate checkInDate;

    private Integer studyMinutes;

    private String note;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
