package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {
    int insert(PostLike postLike);
    PostLike findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);
}

