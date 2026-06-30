package com.coffeechat.domain.chat.dto;

import com.coffeechat.domain.chat.entity.MessageType;

public record ChatMessagePayload(
        Long roomId,
        String content,
        MessageType messageType
) {}
