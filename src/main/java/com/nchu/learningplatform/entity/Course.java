// learning-platform/src/main/java/com/nchu/learningplatform/entity/Course.java
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
}
