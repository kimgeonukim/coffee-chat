package com.coffeechat.domain.chat.dto;

import com.coffeechat.domain.chat.entity.ChatRoom;
import com.coffeechat.domain.chat.entity.ChatRoomType;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long id,
        String name,
        ChatRoomType type,
        int memberCount,
        LocalDateTime createdAt
) {
    public static ChatRoomResponse from(ChatRoom room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getName(),
                room.getType(),
                room.getMembers().size(),
                room.getCreatedAt()
        );
    }
}
