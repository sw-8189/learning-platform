package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message message);
    
    // 获取两个用户之间的所有消息
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // 获取用户的所有会话列表（每个会话显示最新一条消息）
    List<Message> findConversationsByUserId(@Param("userId") Long userId);
    
    // 获取未读消息数量
    int countUnread(@Param("userId") Long userId);
    
    // 获取与某个用户的未读消息数量
    int countUnreadBySender(@Param("userId") Long userId, @Param("senderId") Long senderId);
    
    // 标记消息为已读
    int markAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);
    
    // 标记单条消息为已读
    int markMessageAsRead(@Param("messageId") Long messageId, @Param("userId") Long userId);
}

