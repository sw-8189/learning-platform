//package com.nchu.learningplatform;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class LearningPlatformApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(LearningPlatformApplication.class, args);
//    }
//
//}

package com.nchu.learningplatform;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
@MapperScan("com.nchu.learningplatform.mapper")
public class LearningPlatformApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(LearningPlatformApplication.class, args);

        // 启动完成后尝试自动打开浏览器
        try {
            String url = "http://localhost:8080/index.html";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("请在浏览器中访问: " + url);
            }
        } catch (Exception e) {
            System.out.println("无法自动打开浏览器，请手动访问 http://localhost:8080/index.html");
        }
    }
}
