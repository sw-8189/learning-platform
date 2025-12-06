package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {
    int insert(Post post);
    Post findById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);
    List<Post> pageQuery(@Param("page") Integer page, @Param("size") Integer size, 
                        @Param("offset") Integer offset, @Param("type") String type,
                        @Param("category") String category, @Param("keyword") String keyword,
                        @Param("userId") Long userId, @Param("courseId") Long courseId,
                        @Param("currentUserId") Long currentUserId);
    long count(@Param("type") String type, @Param("category") String category,
               @Param("keyword") String keyword, @Param("userId") Long userId,
               @Param("courseId") Long courseId, @Param("currentUserId") Long currentUserId);
    int update(Post post);
    int delete(@Param("id") Long id);
    int incrementViewCount(@Param("id") Long id);
    int incrementLikeCount(@Param("id") Long id);
    int decrementLikeCount(@Param("id") Long id);
    int incrementCommentCount(@Param("id") Long id);
    int incrementFavoriteCount(@Param("id") Long id);
    int decrementFavoriteCount(@Param("id") Long id);
    long countAll();
    List<Post> findHotPosts(@Param("limit") Integer limit, @Param("currentUserId") Long currentUserId);
    List<Post> findTopPosts(@Param("currentUserId") Long currentUserId);
    
    // 用户点赞/收藏的帖子列表
    List<Post> findLikedPostsByUser(@Param("userId") Long userId,
                                    @Param("size") Integer size,
                                    @Param("offset") Integer offset);
    long countLikedPostsByUser(@Param("userId") Long userId);
    
    List<Post> findFavoritePostsByUser(@Param("userId") Long userId,
                                       @Param("size") Integer size,
                                       @Param("offset") Integer offset);
    long countFavoritePostsByUser(@Param("userId") Long userId);
}

