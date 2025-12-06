package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.util.FileUploadUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload/image")
public class UploadController {

    private static final long MAX_SIZE = FileUploadUtils.MAX_COURSE_COVER_SIZE; // 5MB
    private static final int MIN_WIDTH = 200;
    private static final int MIN_HEIGHT = 120;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> res = new HashMap<>();
        if (file == null || file.isEmpty()) {
            res.put("valid", false);
            res.put("message", "文件为空");
            return ResponseEntity.ok(res);
        }

        if (file.getSize() > MAX_SIZE) {
            res.put("valid", false);
            res.put("message", "图片大小不能超过 5MB");
            return ResponseEntity.ok(res);
        }

        String originalFilename = file.getOriginalFilename();
        if (!FileUploadUtils.isValidImageType(originalFilename)) {
            res.put("valid", false);
            res.put("message", "不支持的图片格式，仅支持 JPG/JPEG/PNG/GIF");
            return ResponseEntity.ok(res);
        }

        try (InputStream in = file.getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                res.put("valid", false);
                res.put("message", "无法识别的图片或图片已损坏");
                return ResponseEntity.ok(res);
            }
            int w = img.getWidth();
            int h = img.getHeight();
            res.put("width", w);
            res.put("height", h);
            if (w < MIN_WIDTH || h < MIN_HEIGHT) {
                res.put("valid", false);
                res.put("message", String.format("图片尺寸过小，至少 %dx%d", MIN_WIDTH, MIN_HEIGHT));
                return ResponseEntity.ok(res);
            }

            res.put("valid", true);
            res.put("message", "校验通过");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("valid", false);
            res.put("message", "校验过程出错");
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> res = new HashMap<>();
        if (file == null || file.isEmpty()) {
            res.put("error", "文件为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        try {
            // 保存到 uploads/course 子目录
            String url = FileUploadUtils.uploadFile(file, "uploads", "course");
            res.put("url", url);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException iae) {
            res.put("error", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (Exception e) {
            res.put("error", "保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
