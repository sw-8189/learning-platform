package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper {
    int insert(Notification notification);
    List<Notification> findByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    int countUnread(@Param("userId") Long userId);
    int markAsRead(@Param("id") Long id);
    int markAllAsRead(@Param("userId") Long userId);
}

