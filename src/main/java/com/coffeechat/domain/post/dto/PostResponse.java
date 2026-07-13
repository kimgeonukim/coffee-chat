package com.coffeechat.domain.post.dto;

import com.coffeechat.domain.post.entity.Category;
import com.coffeechat.domain.post.entity.Post;
import com.coffeechat.domain.post.entity.SubCategory;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        Category category,
        SubCategory subCategory,
        Long authorId,
        String authorNickname,
        boolean closed,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getSubCategory(),
                post.getAuthor().getId(),
                post.getAuthor().getNickname(),
                post.isClosed(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
