package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.entity.Message;
import com.nchu.learningplatform.mapper.MessageMapper;
import com.nchu.learningplatform.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId == null || receiverId == null || content == null || content.trim().isEmpty()) {
            throw new RuntimeException("参数不能为空");
        }
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("不能给自己发消息");
        }
        
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content.trim());
        message.setIsRead(false);
        
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return messageMapper.findConversation(userId1, userId2);
    }

    @Override
    public List<Message> getConversations(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return messageMapper.findConversationsByUserId(userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        return messageMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long senderId) {
        if (userId == null || senderId == null) {
            return;
        }
        messageMapper.markAsRead(userId, senderId);
    }
    
    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return;
        }
        // 直接标记为已读，数据库层面会验证receiver_id
        messageMapper.markMessageAsRead(messageId, userId);
    }
}

