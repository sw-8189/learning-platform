package com.nchu.learningplatform.entity;

import lombok.Data;

@Data
public class Course {

    private Long id;

    private String title;

    private String description;

    /** 难度级别：初级/中级/高级 */
    private String level;

    /** 分类：Java / 前端 / 数据库 等 */
    private String category;

    /** 封面图片 URL */
    private String coverUrl;

    private String teacher;

    private Double price;

    /** 课程标签：多个标签用逗号分隔，包含学习偏好和课程兴趣 */
    private String tags;

    /** 课程详细描述 */
    private String detailDescription;

    /** 讲师介绍 */
    private String teacherIntro;

    /** 课程大纲，JSON格式存储 */
    private String courseOutline;

    /** 课程图片，JSON格式存储多个图片URL */
    private String courseImages;

    /** 课程时长，如：40小时 */
    private String duration;

    /** 学习人数 */
    private Integer studentCount;

    /** 课程评分，0-5分 */
    private Double rating;
}
