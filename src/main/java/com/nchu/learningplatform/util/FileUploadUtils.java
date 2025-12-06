package com.nchu.learningplatform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 * 统一处理文件上传逻辑
 */
public class FileUploadUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);
    
    // 允许的文件类型
    public static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png", ".gif"};
    public static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024; // 2MB
    public static final long MAX_COURSE_COVER_SIZE = 5 * 1024 * 1024; // 5MB
    
    /**
     * 验证文件类型
     */
    public static boolean isValidImageType(String filename) {
        if (filename == null) {
            return false;
        }
        String lowerFilename = filename.toLowerCase();
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (lowerFilename.endsWith(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证文件大小
     */
    public static boolean isValidSize(MultipartFile file, long maxSize) {
        return file != null && file.getSize() <= maxSize;
    }
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param uploadDir 上传目录（相对路径或绝对路径）
     * @param subDir 子目录（如 "avatar" 或 "course"）
     * @return 访问URL路径（如 "/uploads/avatar/filename.jpg"）
     */
    public static String uploadFile(MultipartFile file, String uploadDir, String subDir) throws Exception {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 验证文件大小
        long maxSize = "avatar".equals(subDir) ? MAX_AVATAR_SIZE : MAX_COURSE_COVER_SIZE;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }
        
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageType(originalFilename)) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 JPG、JPEG、PNG、GIF 格式");
        }
        
        // 解析上传目录
        Path dir = Paths.get(uploadDir);
        if (!dir.isAbsolute()) {
            dir = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);
        }
        
        // 如果有子目录，添加到路径中
        if (subDir != null && !subDir.isEmpty()) {
            dir = dir.resolve(subDir);
        }
        
        // 创建目录
        Files.createDirectories(dir);
        
        // 生成文件名
        String ext = "";
        if (originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;
        
        // 保存文件
        Path target = dir.resolve(filename);
        File targetFile = target.toFile();
        if (targetFile == null) {
            throw new RuntimeException("无法创建目标文件");
        }
        
        file.transferTo(targetFile);
        
        // 返回访问路径
        // 格式：/uploads/subDir/filename.ext
        String accessPath = "/uploads/" + (subDir != null ? subDir + "/" : "") + filename;
        logger.info("文件上传成功: {} -> {}", originalFilename, accessPath);
        
        return accessPath;
    }
}

