package com.nchu.learningplatform.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyBatisConfig implements WebMvcConfigurer {

    @Value("${avatar.upload-dir:uploads/avatar}")
    private String avatarUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地上传头像目录为 /uploads/** 静态资源
        String location = "file:" + (avatarUploadDir.endsWith("/") ? avatarUploadDir : avatarUploadDir + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        // 保留默认 static 目录映射
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
