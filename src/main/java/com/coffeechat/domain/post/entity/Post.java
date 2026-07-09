package com.coffeechat.domain.post.entity;

import com.coffeechat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private boolean closed;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Post(String title, String content, Category category, SubCategory subCategory, User author) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.subCategory = subCategory;
        this.author = author;
        this.closed = false;
    }

    public void update(String title, String content, Category category, SubCategory subCategory) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.subCategory = subCategory;
    }

    public void close() {
        this.closed = true;
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}
