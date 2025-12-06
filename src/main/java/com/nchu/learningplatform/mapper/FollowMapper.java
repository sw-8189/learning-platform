package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FollowMapper {
    int insert(Follow follow);
    Follow findByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    int delete(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    List<Long> findFollowingIds(@Param("followerId") Long followerId);
    List<Long> findFollowerIds(@Param("followingId") Long followingId);
    int countFollowing(@Param("userId") Long userId);
    int countFollowers(@Param("userId") Long userId);
}

