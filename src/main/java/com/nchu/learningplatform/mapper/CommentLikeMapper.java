package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentLikeMapper {
    int insert(CommentLike commentLike);
    CommentLike findByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);
}

