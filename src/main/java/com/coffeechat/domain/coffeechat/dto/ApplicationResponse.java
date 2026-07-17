package com.coffeechat.domain.coffeechat.dto;

import com.coffeechat.domain.coffeechat.entity.ApplicationStatus;
import com.coffeechat.domain.coffeechat.entity.CoffeeChatApplication;

import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long postId,
        String postTitle,
        Long applicantId,
        String applicantNickname,
        Long receiverId,
        String receiverNickname,
        String message,
        ApplicationStatus status,
        Long chatRoomId,
        LocalDateTime scheduledAt,
        LocalDateTime createdAt
) {
    public static ApplicationResponse from(CoffeeChatApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getPost().getId(),
                application.getPost().getTitle(),
                application.getApplicant().getId(),
                application.getApplicant().getNickname(),
                application.getReceiver().getId(),
                application.getReceiver().getNickname(),
                application.getMessage(),
                application.getStatus(),
                application.getChatRoom() != null ? application.getChatRoom().getId() : null,
                application.getScheduledAt(),
                application.getCreatedAt()
        );
    }
}
