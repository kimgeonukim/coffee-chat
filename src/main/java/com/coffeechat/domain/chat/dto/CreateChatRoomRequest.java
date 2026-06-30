package com.coffeechat.domain.chat.dto;

import com.coffeechat.domain.chat.entity.ChatRoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateChatRoomRequest(
        @NotBlank(message = "채팅방 이름은 필수입니다")
        @Size(max = 50, message = "채팅방 이름은 50자 이하여야 합니다")
        String name,

        @NotNull(message = "채팅방 타입은 필수입니다")
        ChatRoomType type,

        List<Long> memberIds
) {}
