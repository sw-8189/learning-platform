package com.nchu.learningplatform.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileUploadUtils 单元测试
 * 测试文件上传工具类的各种功能
 */
@DisplayName("文件上传工具类测试")
class FileUploadUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("测试图片类型验证 - 正常场景：JPG格式")
    void testIsValidImageType_JPG() {
        // Arrange
        String filename = "test.jpg";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertTrue(result, "JPG格式应该被接受");
    }

    @Test
    @DisplayName("测试图片类型验证 - 正常场景：JPEG格式")
    void testIsValidImageType_JPEG() {
        // Arrange
        String filename = "test.jpeg";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertTrue(result, "JPEG格式应该被接受");
    }

    @Test
    @DisplayName("测试图片类型验证 - 正常场景：PNG格式")
    void testIsValidImageType_PNG() {
        // Arrange
        String filename = "test.png";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertTrue(result, "PNG格式应该被接受");
    }

    @Test
    @DisplayName("测试图片类型验证 - 正常场景：GIF格式")
    void testIsValidImageType_GIF() {
        // Arrange
        String filename = "test.gif";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertTrue(result, "GIF格式应该被接受");
    }

    @Test
    @DisplayName("测试图片类型验证 - 正常场景：大写扩展名")
    void testIsValidImageType_UppercaseExtension() {
        // Arrange
        String filename = "test.JPG";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertTrue(result, "大写扩展名应该被接受");
    }

    @Test
    @DisplayName("测试图片类型验证 - 异常情况：null文件名")
    void testIsValidImageType_NullFilename() {
        // Arrange
        String filename = null;

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertFalse(result, "null文件名应该返回false");
    }

    @Test
    @DisplayName("测试图片类型验证 - 异常情况：不支持的文件类型")
    void testIsValidImageType_UnsupportedType() {
        // Arrange
        String filename = "test.txt";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertFalse(result, "不支持的文件类型应该返回false");
    }

    @Test
    @DisplayName("测试图片类型验证 - 异常情况：无扩展名")
    void testIsValidImageType_NoExtension() {
        // Arrange
        String filename = "test";

        // Act
        boolean result = FileUploadUtils.isValidImageType(filename);

        // Assert
        assertFalse(result, "无扩展名的文件应该返回false");
    }

    @Test
    @DisplayName("测试文件大小验证 - 正常场景：文件大小在限制内")
    void testIsValidSize_ValidSize() {
        // Arrange
        byte[] content = new byte[1024]; // 1KB
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        long maxSize = 2 * 1024 * 1024; // 2MB

        // Act
        boolean result = FileUploadUtils.isValidSize(file, maxSize);

        // Assert
        assertTrue(result, "文件大小在限制内应该返回true");
    }

    @Test
    @DisplayName("测试文件大小验证 - 边界条件：文件大小正好等于限制")
    void testIsValidSize_ExactlyMaxSize() {
        // Arrange
        byte[] content = new byte[2 * 1024 * 1024]; // 正好2MB
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        long maxSize = 2 * 1024 * 1024; // 2MB

        // Act
        boolean result = FileUploadUtils.isValidSize(file, maxSize);

        // Assert
        assertTrue(result, "文件大小正好等于限制应该返回true");
    }

    @Test
    @DisplayName("测试文件大小验证 - 异常情况：文件大小超过限制")
    void testIsValidSize_ExceedsMaxSize() {
        // Arrange
        byte[] content = new byte[3 * 1024 * 1024]; // 3MB
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        long maxSize = 2 * 1024 * 1024; // 2MB

        // Act
        boolean result = FileUploadUtils.isValidSize(file, maxSize);

        // Assert
        assertFalse(result, "文件大小超过限制应该返回false");
    }

    @Test
    @DisplayName("测试文件大小验证 - 异常情况：null文件")
    void testIsValidSize_NullFile() {
        // Arrange
        MultipartFile file = null;
        long maxSize = 2 * 1024 * 1024; // 2MB

        // Act
        boolean result = FileUploadUtils.isValidSize(file, maxSize);

        // Assert
        assertFalse(result, "null文件应该返回false");
    }

    @Test
    @DisplayName("测试文件上传 - 正常场景：上传JPG文件")
    void testUploadFile_ValidJPG() throws Exception {
        // Arrange
        byte[] content = "fake image content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act
        String result = FileUploadUtils.uploadFile(file, uploadDir, subDir);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/avatar/"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    @DisplayName("测试文件上传 - 正常场景：上传PNG文件")
    void testUploadFile_ValidPNG() throws Exception {
        // Arrange
        byte[] content = "fake image content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", content);
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act
        String result = FileUploadUtils.uploadFile(file, uploadDir, subDir);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/avatar/"));
        assertTrue(result.endsWith(".png"));
    }

    @Test
    @DisplayName("测试文件上传 - 异常情况：null文件")
    void testUploadFile_NullFile() {
        // Arrange
        MultipartFile file = null;
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            FileUploadUtils.uploadFile(file, uploadDir, subDir);
        }, "null文件应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试文件上传 - 异常情况：空文件")
    void testUploadFile_EmptyFile() {
        // Arrange
        byte[] content = new byte[0];
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            FileUploadUtils.uploadFile(file, uploadDir, subDir);
        }, "空文件应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试文件上传 - 异常情况：文件大小超过限制")
    void testUploadFile_FileTooLarge() {
        // Arrange
        byte[] content = new byte[3 * 1024 * 1024]; // 3MB，超过头像限制2MB
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            FileUploadUtils.uploadFile(file, uploadDir, subDir);
        }, "文件大小超过限制应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试文件上传 - 异常情况：不支持的文件类型")
    void testUploadFile_UnsupportedType() {
        // Arrange
        byte[] content = "fake content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", content);
        String uploadDir = tempDir.toString();
        String subDir = "avatar";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            FileUploadUtils.uploadFile(file, uploadDir, subDir);
        }, "不支持的文件类型应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试文件上传 - 正常场景：无子目录")
    void testUploadFile_NoSubDir() throws Exception {
        // Arrange
        byte[] content = "fake image content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        String uploadDir = tempDir.toString();
        String subDir = null;

        // Act
        String result = FileUploadUtils.uploadFile(file, uploadDir, subDir);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/"));
    }

    @Test
    @DisplayName("测试文件上传 - 正常场景：课程封面（5MB限制）")
    void testUploadFile_CourseCover() throws Exception {
        // Arrange
        byte[] content = new byte[4 * 1024 * 1024]; // 4MB，在课程封面限制5MB内
        MultipartFile file = new MockMultipartFile("file", "cover.jpg", "image/jpeg", content);
        String uploadDir = tempDir.toString();
        String subDir = "course";

        // Act
        String result = FileUploadUtils.uploadFile(file, uploadDir, subDir);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/course/"));
    }
}

