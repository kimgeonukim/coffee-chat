package com.coffeechat.domain.chat.dto;

import com.coffeechat.domain.chat.entity.ChatMessage;
import com.coffeechat.domain.chat.entity.MessageType;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long roomId,
        Long senderId,
        String senderNickname,
        String content,
        MessageType messageType,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }
}
