package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.*;
import com.nchu.learningplatform.service.CommunityService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin
public class CommunityController {

    @Resource
    private CommunityService communityService;

    @Resource
    private AuthController authController;

    // ========== 帖子相关 ==========
    
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
                                        @RequestBody Post post) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            Post createdPost = communityService.createPost(userId, post);
            return ResponseEntity.ok(createdPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "发布失败: " + e.getMessage()));
        }
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = token != null ? authController.getUserIdByToken(token) : null;
        Post post = communityService.getPostById(id, userId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "帖子不存在"));
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long courseId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = token != null ? authController.getUserIdByToken(token) : null;
        PageResult<Post> result = communityService.getPosts(page, size, type, category, keyword, userId, courseId, currentUserId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/hot")
    public ResponseEntity<?> getHotPosts(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = token != null ? authController.getUserIdByToken(token) : null;
        List<Post> posts = communityService.getHotPosts(limit, currentUserId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token,
                                         @RequestBody Post post) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            Post updatedPost = communityService.updatePost(id, userId, post);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 上传帖子附件（例如资料、作业等）
     * 前端先调用此接口上传文件，成功后返回的 URL 填入 Post.attachmentUrl
     */
    @PostMapping("/attachments")
    public ResponseEntity<?> uploadAttachment(@RequestHeader("Authorization") String token,
                                              @RequestParam("file") MultipartFile file) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "上传文件不能为空"));
        }
        try {
            // 将附件保存到 uploads/community 目录
            java.nio.file.Path baseDir = java.nio.file.Paths.get(System.getProperty("user.dir"))
                    .resolve("uploads")
                    .resolve("community");
            java.nio.file.Files.createDirectories(baseDir);

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newName = java.util.UUID.randomUUID() + ext;
            java.nio.file.Path target = baseDir.resolve(newName);
            java.io.File targetFile = target.toFile();
            if (targetFile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "无法创建目标文件"));
            }
            file.transferTo(targetFile);

            String url = "/uploads/community/" + newName;
            return ResponseEntity.ok(Map.of(
                    "message", "附件上传成功",
                    "url", url,
                    "fileName", originalName != null ? originalName : newName
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "附件上传失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            communityService.deletePost(id, userId);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ========== 点赞相关 ==========
    
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.likePost(id, userId);
        return ResponseEntity.ok(Map.of("message", "点赞成功"));
    }

    @DeleteMapping("/posts/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.unlikePost(id, userId);
        return ResponseEntity.ok(Map.of("message", "取消点赞成功"));
    }

    // ========== 收藏相关 ==========
    
    @PostMapping("/posts/{id}/favorite")
    public ResponseEntity<?> favoritePost(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.favoritePost(id, userId);
        return ResponseEntity.ok(Map.of("message", "收藏成功"));
    }

    @DeleteMapping("/posts/{id}/favorite")
    public ResponseEntity<?> unfavoritePost(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.unfavoritePost(id, userId);
        return ResponseEntity.ok(Map.of("message", "取消收藏成功"));
    }
    
    @GetMapping("/posts/favorites")
    public ResponseEntity<?> getFavoritePosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        PageResult<Post> result = communityService.getFavoritePosts(userId, page, size);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/posts/liked")
    public ResponseEntity<?> getLikedPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        PageResult<Post> result = communityService.getLikedPosts(userId, page, size);
        return ResponseEntity.ok(result);
    }

    // ========== 评论相关 ==========
    
    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestHeader("Authorization") String token,
                                           @RequestBody Comment comment) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            Comment createdComment = communityService.createComment(userId, comment);
            return ResponseEntity.ok(createdComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "评论失败: " + e.getMessage()));
        }
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        Long currentUserId = token != null ? authController.getUserIdByToken(token) : null;
        List<Comment> comments = communityService.getCommentsByPostId(postId, currentUserId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token,
                                             @RequestBody Comment comment) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            Comment updatedComment = communityService.updateComment(id, userId, comment);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            communityService.deleteComment(id, userId);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.likeComment(id, userId);
        return ResponseEntity.ok(Map.of("message", "点赞成功"));
    }

    @DeleteMapping("/comments/{id}/like")
    public ResponseEntity<?> unlikeComment(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.unlikeComment(id, userId);
        return ResponseEntity.ok(Map.of("message", "取消点赞成功"));
    }

    @PostMapping("/comments/{id}/best-answer")
    public ResponseEntity<?> setBestAnswer(@PathVariable Long id,
                                            @RequestParam Long postId,
                                            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        try {
            communityService.setBestAnswer(id, postId, userId);
            return ResponseEntity.ok(Map.of("message", "设置成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ========== 关注相关 ==========
    
    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long userId,
                                         @RequestHeader("Authorization") String token) {
        Long followerId = authController.getUserIdByToken(token);
        if (followerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.followUser(followerId, userId);
        return ResponseEntity.ok(Map.of("message", "关注成功"));
    }

    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId,
                                           @RequestHeader("Authorization") String token) {
        Long followerId = authController.getUserIdByToken(token);
        if (followerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.unfollowUser(followerId, userId);
        return ResponseEntity.ok(Map.of("message", "取消关注成功"));
    }

    // ========== 通知相关 ==========
    
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        List<Notification> notifications = communityService.getNotifications(userId, limit);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<?> getUnreadNotificationCount(@RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        int count = communityService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id,
                                                     @RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.markNotificationAsRead(id, userId);
        return ResponseEntity.ok(Map.of("message", "已标记为已读"));
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "未登录或登录已失效"));
        }
        
        communityService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "全部标记为已读"));
    }
}

