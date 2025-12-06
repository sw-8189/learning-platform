package com.nchu.learningplatform.service;

import com.nchu.learningplatform.entity.Message;
import java.util.List;

public interface MessageService {
    /**
     * 发送私信
     */
    Message sendMessage(Long senderId, Long receiverId, String content);
    
    /**
     * 获取两个用户之间的会话消息
     */
    List<Message> getConversation(Long userId1, Long userId2);
    
    /**
     * 获取用户的所有会话列表
     */
    List<Message> getConversations(Long userId);
    
    /**
     * 获取未读消息数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long userId, Long senderId);
    
    /**
     * 标记单条消息为已读
     */
    void markMessageAsRead(Long messageId, Long userId);
}

