package com.coffeechat.domain.user.dto;

import com.coffeechat.domain.user.entity.User;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        String bio,
        String profileImageUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getBio(), user.getProfileImageUrl());
    }
}
