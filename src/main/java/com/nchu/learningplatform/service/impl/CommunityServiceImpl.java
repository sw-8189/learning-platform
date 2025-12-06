package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.dto.PageResult;
import com.nchu.learningplatform.entity.Post;
import com.nchu.learningplatform.entity.Comment;
import com.nchu.learningplatform.entity.CommentLike;
import com.nchu.learningplatform.entity.PostLike;
import com.nchu.learningplatform.entity.Favorite;
import com.nchu.learningplatform.entity.Follow;
import com.nchu.learningplatform.entity.Notification;
import com.nchu.learningplatform.entity.User;
import com.nchu.learningplatform.mapper.PostMapper;
import com.nchu.learningplatform.mapper.CommentMapper;
import com.nchu.learningplatform.mapper.PostLikeMapper;
import com.nchu.learningplatform.mapper.CommentLikeMapper;
import com.nchu.learningplatform.mapper.FavoriteMapper;
import com.nchu.learningplatform.mapper.FollowMapper;
import com.nchu.learningplatform.mapper.NotificationMapper;
import com.nchu.learningplatform.mapper.UserMapper;
import com.nchu.learningplatform.mapper.CourseMapper;
import com.nchu.learningplatform.service.CommunityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private FollowMapper followMapper;

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public Post createPost(Long userId, Post post) {
        post.setUserId(userId);
        post.setStatus("published");
        if (post.getVisibility() == null || post.getVisibility().isEmpty()) {
            post.setVisibility("PUBLIC");
        }
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);
        post.setIsTop(false);
        if (post.getIsResolved() == null) {
            post.setIsResolved(false);
        }
        postMapper.insert(post);
        
        // 增加用户积分和贡献值（简化处理，实际应该通过SQL更新）
        // User user = userMapper.findById(userId);
        // if (user != null) {
        //     user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + 5);
        //     user.setContribution((user.getContribution() != null ? user.getContribution() : 0) + 1);
        //     userMapper.updateUserInfo(user);
        // }
        
        return getPostById(post.getId(), userId);
    }

    @Override
    public Post getPostById(Long postId, Long currentUserId) {
        Post post = postMapper.findById(postId, currentUserId);
        if (post == null) {
            return null;
        }
        
        // 检查权限：私密帖子只能作者查看
        if ("PRIVATE".equals(post.getVisibility()) && (currentUserId == null || !post.getUserId().equals(currentUserId))) {
            return null;
        }
        
        // 增加浏览量
        postMapper.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);
        
        // 检查当前用户是否点赞、收藏
        if (currentUserId != null) {
            post.setIsLiked(postLikeMapper.findByPostIdAndUserId(postId, currentUserId) != null);
            post.setIsFavorited(favoriteMapper.findByPostIdAndUserId(postId, currentUserId) != null);
        }
        
        return post;
    }

    @Override
    public PageResult<Post> getPosts(Integer page, Integer size, String type, String category, 
                                     String keyword, Long userId, Long courseId, Long currentUserId) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.pageQuery(page, size, offset, type, category, keyword, userId, courseId, currentUserId);
        long total = postMapper.count(type, category, keyword, userId, courseId, currentUserId);
        
        // 检查当前用户的点赞和收藏状态
        if (currentUserId != null) {
            for (Post post : posts) {
                post.setIsLiked(postLikeMapper.findByPostIdAndUserId(post.getId(), currentUserId) != null);
                post.setIsFavorited(favoriteMapper.findByPostIdAndUserId(post.getId(), currentUserId) != null);
            }
        }
        
        return new PageResult<>(total, page, size, posts);
    }

    @Override
    public List<Post> getHotPosts(Integer limit, Long currentUserId) {
        List<Post> posts = postMapper.findHotPosts(limit, currentUserId);
        
        if (currentUserId != null) {
            for (Post post : posts) {
                post.setIsLiked(postLikeMapper.findByPostIdAndUserId(post.getId(), currentUserId) != null);
                post.setIsFavorited(favoriteMapper.findByPostIdAndUserId(post.getId(), currentUserId) != null);
            }
        }
        
        return posts;
    }

    @Override
    @Transactional
    public Post updatePost(Long postId, Long userId, Post post) {
        Post existingPost = postMapper.findById(postId, userId);
        if (existingPost == null || !existingPost.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此帖子");
        }
        
        post.setId(postId);
        postMapper.update(post);
        return getPostById(postId, userId);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.findById(postId, userId);
        if (post == null || !post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此帖子");
        }
        postMapper.delete(postId);
    }

    @Override
    @Transactional
    public void likePost(Long postId, Long userId) {
        PostLike existing = postLikeMapper.findByPostIdAndUserId(postId, userId);
        if (existing != null) {
            return; // 已点赞
        }
        
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeMapper.insert(postLike);
        postMapper.incrementLikeCount(postId);
        
        // 创建通知
        Post post = postMapper.findById(postId, userId);
        if (post != null && !post.getUserId().equals(userId)) {
            createNotification(post.getUserId(), "like", postId, userId, "点赞了你的帖子");
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        PostLike existing = postLikeMapper.findByPostIdAndUserId(postId, userId);
        if (existing == null) {
            return;
        }
        postLikeMapper.delete(postId, userId);
        postMapper.decrementLikeCount(postId);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        CommentLike existing = commentLikeMapper.findByCommentIdAndUserId(commentId, userId);
        if (existing != null) {
            return;
        }
        
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLikeMapper.insert(commentLike);
        commentMapper.incrementLikeCount(commentId);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        CommentLike existing = commentLikeMapper.findByCommentIdAndUserId(commentId, userId);
        if (existing == null) {
            return;
        }
        commentLikeMapper.delete(commentId, userId);
        commentMapper.decrementLikeCount(commentId);
    }

    @Override
    @Transactional
    public void favoritePost(Long postId, Long userId) {
        Favorite existing = favoriteMapper.findByPostIdAndUserId(postId, userId);
        if (existing != null) {
            return;
        }
        
        Favorite favorite = new Favorite();
        favorite.setPostId(postId);
        favorite.setUserId(userId);
        favoriteMapper.insert(favorite);
        postMapper.incrementFavoriteCount(postId);
    }

    @Override
    @Transactional
    public void unfavoritePost(Long postId, Long userId) {
        Favorite existing = favoriteMapper.findByPostIdAndUserId(postId, userId);
        if (existing == null) {
            return;
        }
        favoriteMapper.delete(postId, userId);
        postMapper.decrementFavoriteCount(postId);
    }

    @Override
    public PageResult<Post> getFavoritePosts(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findFavoritePostsByUser(userId, size, offset);
        long total = postMapper.countFavoritePostsByUser(userId);
        
        // 当前用户就是自己，收藏列表中一定是已收藏；同时检查是否点赞
        for (Post post : posts) {
            post.setIsFavorited(true);
            post.setIsLiked(postLikeMapper.findByPostIdAndUserId(post.getId(), userId) != null);
        }
        
        return new PageResult<>(total, page, size, posts);
    }
    
    @Override
    public PageResult<Post> getLikedPosts(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findLikedPostsByUser(userId, size, offset);
        long total = postMapper.countLikedPostsByUser(userId);
        
        for (Post post : posts) {
            post.setIsLiked(true);
            post.setIsFavorited(favoriteMapper.findByPostIdAndUserId(post.getId(), userId) != null);
        }
        
        return new PageResult<>(total, page, size, posts);
    }

    @Override
    @Transactional
    public Comment createComment(Long userId, Comment comment) {
        comment.setUserId(userId);
        commentMapper.insert(comment);
        
        // 增加帖子评论数
        postMapper.incrementCommentCount(comment.getPostId());
        
        // 创建通知
        Post post = postMapper.findById(comment.getPostId(), userId);
        if (post != null && !post.getUserId().equals(userId)) {
            String content = comment.getParentId() == null ? "评论了你的帖子" : "回复了你的评论";
            createNotification(post.getUserId(), "comment", comment.getPostId(), userId, content);
        }
        
        // 如果是回复评论，通知被回复的用户
        if (comment.getParentId() != null) {
            Comment parentComment = commentMapper.findById(comment.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                createNotification(parentComment.getUserId(), "reply", comment.getPostId(), userId, "回复了你的评论");
            }
        }
        
        return getCommentWithUser(comment.getId(), userId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId, Long currentUserId) {
        List<Comment> comments = commentMapper.findByPostId(postId);
        
        // 加载子评论
        for (Comment comment : comments) {
            List<Comment> replies = commentMapper.findByParentId(comment.getId());
            if (currentUserId != null) {
                for (Comment reply : replies) {
                    reply.setIsLiked(commentLikeMapper.findByCommentIdAndUserId(reply.getId(), currentUserId) != null);
                }
            }
            comment.setReplies(replies);
            
            if (currentUserId != null) {
                comment.setIsLiked(commentLikeMapper.findByCommentIdAndUserId(comment.getId(), currentUserId) != null);
            }
        }
        
        return comments;
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, Long userId, Comment comment) {
        Comment existing = commentMapper.findById(commentId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此评论");
        }
        
        comment.setId(commentId);
        commentMapper.update(comment);
        return getCommentWithUser(commentId, userId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        Long postId = comment.getPostId();
        commentMapper.delete(commentId);
        // 更新帖子评论数（简化处理，实际应该通过SQL更新）
        Post post = postMapper.findById(postId, userId);
        if (post != null) {
            post.setCommentCount(Math.max(0, (post.getCommentCount() != null ? post.getCommentCount() : 0) - 1));
            postMapper.update(post);
        }
    }

    @Override
    @Transactional
    public void setBestAnswer(Long commentId, Long postId, Long userId) {
        Post post = postMapper.findById(postId, userId);
        if (post == null || !post.getUserId().equals(userId) || !"question".equals(post.getType())) {
            throw new RuntimeException("无权设置最佳回答");
        }
        
        commentMapper.unsetBestAnswer(postId);
        commentMapper.setBestAnswer(commentId);
        post.setIsResolved(true);
        postMapper.update(post);
        
        // 通知评论作者
        Comment comment = commentMapper.findById(commentId);
        if (comment != null) {
            createNotification(comment.getUserId(), "best_answer", postId, userId, "你的回答被采纳为最佳答案");
        }
    }

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return;
        }
        
        Follow existing = followMapper.findByFollowerAndFollowing(followerId, followingId);
        if (existing != null) {
            return;
        }
        
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followMapper.insert(follow);
        
        // 创建通知
        createNotification(followingId, "follow", null, followerId, "关注了你");
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        followMapper.delete(followerId, followingId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.findByFollowerAndFollowing(followerId, followingId) != null;
    }

    @Override
    public List<User> getFollowingUsers(Long userId) {
        List<Long> followingIds = followMapper.findFollowingIds(userId);
        return followingIds.stream()
                .map(userMapper::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowerUsers(Long userId) {
        List<Long> followerIds = followMapper.findFollowerIds(userId);
        return followerIds.stream()
                .map(userMapper::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getNotifications(Long userId, Integer limit) {
        return notificationMapper.findByUserId(userId, limit);
    }

    @Override
    public int getUnreadNotificationCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.findByUserId(userId, null).stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElse(null);
        if (notification != null) {
            notificationMapper.markAsRead(notificationId);
        }
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public UserStats getUserStats(Long userId) {
        UserStats stats = new UserStats();
        // 简化实现，实际应该通过SQL聚合查询
        return stats;
    }

    private Comment getCommentWithUser(Long commentId, Long currentUserId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment != null && currentUserId != null) {
            comment.setIsLiked(commentLikeMapper.findByCommentIdAndUserId(commentId, currentUserId) != null);
        }
        return comment;
    }

    private void createNotification(Long userId, String type, Long relatedId, Long fromUserId, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setFromUserId(fromUserId);
        notification.setContent(content);
        notification.setIsRead(false);
        notificationMapper.insert(notification);
    }
}

