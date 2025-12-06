package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {
    int insert(Favorite favorite);
    Favorite findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
    int delete(@Param("postId") Long postId, @Param("userId") Long userId);
}

