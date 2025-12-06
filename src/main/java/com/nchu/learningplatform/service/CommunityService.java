package com.nchu.learningplatform.service;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.*;

import java.util.List;

public interface CommunityService {
    // 帖子相关
    Post createPost(Long userId, Post post);
    Post getPostById(Long postId, Long currentUserId);
    PageResult<Post> getPosts(Integer page, Integer size, String type, String category, String keyword, Long userId, Long courseId, Long currentUserId);
    List<Post> getHotPosts(Integer limit, Long currentUserId);
    Post updatePost(Long postId, Long userId, Post post);
    void deletePost(Long postId, Long userId);
    
    // 点赞相关
    void likePost(Long postId, Long userId);
    void unlikePost(Long postId, Long userId);
    void likeComment(Long commentId, Long userId);
    void unlikeComment(Long commentId, Long userId);
    
    // 收藏相关
    void favoritePost(Long postId, Long userId);
    void unfavoritePost(Long postId, Long userId);
    PageResult<Post> getFavoritePosts(Long userId, Integer page, Integer size);
    PageResult<Post> getLikedPosts(Long userId, Integer page, Integer size);
    
    // 评论相关
    Comment createComment(Long userId, Comment comment);
    List<Comment> getCommentsByPostId(Long postId, Long currentUserId);
    Comment updateComment(Long commentId, Long userId, Comment comment);
    void deleteComment(Long commentId, Long userId);
    void setBestAnswer(Long commentId, Long postId, Long userId);
    
    // 关注相关
    void followUser(Long followerId, Long followingId);
    void unfollowUser(Long followerId, Long followingId);
    boolean isFollowing(Long followerId, Long followingId);
    List<User> getFollowingUsers(Long userId);
    List<User> getFollowerUsers(Long userId);
    
    // 通知相关
    List<Notification> getNotifications(Long userId, Integer limit);
    int getUnreadNotificationCount(Long userId);
    void markNotificationAsRead(Long notificationId, Long userId);
    void markAllNotificationsAsRead(Long userId);
    
    // 用户统计
    UserStats getUserStats(Long userId);
    
    class UserStats {
        public int postCount;
        public int commentCount;
        public int likeCount;
        public int favoriteCount;
        public int followingCount;
        public int followerCount;
    }
}

