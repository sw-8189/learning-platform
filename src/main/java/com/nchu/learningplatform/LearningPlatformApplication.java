package com.nchu.learningplatform;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
@MapperScan("com.nchu.learningplatform.mapper")
public class LearningPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningPlatformApplication.class, args);

        // 启动完成后尝试自动打开浏览器
        try {
            String url = "http://localhost:8080/index.html";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Please open your browser and visit: " + url);
            }
        } catch (Exception e) {
            System.out.println("Unable to open browser automatically. Please visit http://localhost:8080/index.html manually");
        }
    }
}
