package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);
    Comment findById(@Param("id") Long id);
    List<Comment> findByPostId(@Param("postId") Long postId);
    List<Comment> findByParentId(@Param("parentId") Long parentId);
    int update(Comment comment);
    int delete(@Param("id") Long id);
    int incrementLikeCount(@Param("id") Long id);
    int decrementLikeCount(@Param("id") Long id);
    int setBestAnswer(@Param("id") Long id);
    int unsetBestAnswer(@Param("postId") Long postId);
    long countAll();
}

