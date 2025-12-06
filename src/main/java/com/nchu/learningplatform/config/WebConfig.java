package com.nchu.learningplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于配置请求大小限制等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot 3.x 中，请求大小限制通过 application.yml 配置
    // 这里不需要额外代码，配置已在 application.yml 中完成
}

