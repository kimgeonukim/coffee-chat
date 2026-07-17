package com.coffeechat.domain.coffeechat.entity;

import com.coffeechat.domain.chat.entity.ChatRoom;
import com.coffeechat.domain.post.entity.Post;
import com.coffeechat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_chat_applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CoffeeChatApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private LocalDateTime scheduledAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CoffeeChatApplication(Post post, User applicant, User receiver, String message) {
        this.post = post;
        this.applicant = applicant;
        this.receiver = receiver;
        this.message = message;
        this.status = ApplicationStatus.PENDING;
    }

    public void accept(ChatRoom chatRoom) {
        this.status = ApplicationStatus.ACCEPTED;
        this.chatRoom = chatRoom;
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isAccepted() {
        return this.status == ApplicationStatus.ACCEPTED;
    }

    public boolean isCompleted() {
        return this.status == ApplicationStatus.COMPLETED;
    }

    public void schedule(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void complete() {
        this.status = ApplicationStatus.COMPLETED;
    }

    public boolean isReceiver(Long userId) {
        return this.receiver.getId().equals(userId);
    }

    public boolean isParticipant(Long userId) {
        return this.applicant.getId().equals(userId) || this.receiver.getId().equals(userId);
    }
}
