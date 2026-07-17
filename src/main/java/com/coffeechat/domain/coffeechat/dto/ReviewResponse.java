package com.coffeechat.domain.coffeechat.dto;

import com.coffeechat.domain.coffeechat.entity.CoffeeChatReview;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long applicationId,
        Long reviewerId,
        String reviewerNickname,
        Long revieweeId,
        String revieweeNickname,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(CoffeeChatReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getApplication().getId(),
                review.getReviewer().getId(),
                review.getReviewer().getNickname(),
                review.getReviewee().getId(),
                review.getReviewee().getNickname(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
