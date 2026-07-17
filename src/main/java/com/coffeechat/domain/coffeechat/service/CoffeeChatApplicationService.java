package com.coffeechat.domain.coffeechat.service;

import com.coffeechat.domain.chat.entity.ChatRoom;
import com.coffeechat.domain.chat.entity.ChatRoomMember;
import com.coffeechat.domain.chat.entity.ChatRoomType;
import com.coffeechat.domain.chat.repository.ChatRoomMemberRepository;
import com.coffeechat.domain.chat.repository.ChatRoomRepository;
import com.coffeechat.domain.coffeechat.dto.ApplicationResponse;
import com.coffeechat.domain.coffeechat.dto.ApplyRequest;
import com.coffeechat.domain.coffeechat.dto.ReviewRequest;
import com.coffeechat.domain.coffeechat.dto.ReviewResponse;
import com.coffeechat.domain.coffeechat.dto.ScheduleRequest;
import com.coffeechat.domain.coffeechat.entity.CoffeeChatApplication;
import com.coffeechat.domain.coffeechat.entity.CoffeeChatReview;
import com.coffeechat.domain.coffeechat.repository.CoffeeChatApplicationRepository;
import com.coffeechat.domain.coffeechat.repository.CoffeeChatReviewRepository;
import com.coffeechat.domain.notification.service.SseEmitterService;
import com.coffeechat.domain.post.entity.Post;
import com.coffeechat.domain.post.repository.PostRepository;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatApplicationService {

    private final CoffeeChatApplicationRepository applicationRepository;
    private final CoffeeChatReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public ApplicationResponse apply(Long applicantId, Long postId, ApplyRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (post.isClosed()) {
            throw new BusinessException(ErrorCode.POST_ALREADY_CLOSED);
        }

        if (post.isAuthor(applicantId)) {
            throw new BusinessException(ErrorCode.CANNOT_APPLY_OWN_POST);
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CoffeeChatApplication application = CoffeeChatApplication.builder()
                .post(post)
                .applicant(applicant)
                .receiver(post.getAuthor())
                .message(request != null ? request.message() : null)
                .build();

        applicationRepository.save(application);

        // 게시글 작성자에게 SSE 알림
        sseEmitterService.notify(
                post.getAuthor().getId(),
                "newApplication",
                applicant.getNickname() + "님이 커피챗을 신청했습니다."
        );

        return ApplicationResponse.from(application);
    }

    public List<ApplicationResponse> getReceivedApplications(Long userId) {
        return applicationRepository.findReceivedByUserId(userId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    public List<ApplicationResponse> getSentApplications(Long userId) {
        return applicationRepository.findSentByUserId(userId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @Transactional
    public ApplicationResponse accept(Long userId, Long applicationId) {
        CoffeeChatApplication application = getApplicationOrThrow(applicationId);
        validateReceiver(application, userId);
        validatePending(application);

        // 1:1 채팅방 생성
        String roomName = application.getApplicant().getNickname() + " - " + application.getReceiver().getNickname();
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .type(ChatRoomType.DIRECT)
                .build();
        chatRoomRepository.save(chatRoom);

        chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(chatRoom).user(application.getApplicant()).build());
        chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(chatRoom).user(application.getReceiver()).build());

        application.accept(chatRoom);

        // 신청자에게 SSE 알림
        sseEmitterService.notify(
                application.getApplicant().getId(),
                "applicationAccepted",
                application.getReceiver().getNickname() + "님이 커피챗 신청을 수락했습니다."
        );

        return ApplicationResponse.from(application);
    }

    @Transactional
    public ApplicationResponse reject(Long userId, Long applicationId) {
        CoffeeChatApplication application = getApplicationOrThrow(applicationId);
        validateReceiver(application, userId);
        validatePending(application);

        application.reject();

        // 신청자에게 SSE 알림
        sseEmitterService.notify(
                application.getApplicant().getId(),
                "applicationRejected",
                application.getReceiver().getNickname() + "님이 커피챗 신청을 거절했습니다."
        );

        return ApplicationResponse.from(application);
    }

    @Transactional
    public ApplicationResponse schedule(Long userId, Long applicationId, ScheduleRequest request) {
        CoffeeChatApplication application = getApplicationOrThrow(applicationId);

        if (!application.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.NOT_APPLICATION_PARTICIPANT);
        }
        if (!application.isAccepted()) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_ACCEPTED);
        }

        application.schedule(request.scheduledAt());

        Long otherId = application.getApplicant().getId().equals(userId)
                ? application.getReceiver().getId()
                : application.getApplicant().getId();

        User me = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        sseEmitterService.notify(
                otherId,
                "scheduleProposed",
                me.getNickname() + "님이 커피챗 일정을 " + request.scheduledAt() + "으로 제안했습니다."
        );

        return ApplicationResponse.from(application);
    }

    @Transactional
    public ApplicationResponse complete(Long userId, Long applicationId) {
        CoffeeChatApplication application = getApplicationOrThrow(applicationId);

        if (!application.isParticipant(userId)) {
            throw new BusinessException(ErrorCode.NOT_APPLICATION_PARTICIPANT);
        }
        if (!application.isAccepted()) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_ACCEPTED);
        }

        application.complete();

        Long otherId = application.getApplicant().getId().equals(userId)
                ? application.getReceiver().getId()
                : application.getApplicant().getId();

        sseEmitterService.notify(otherId, "coffeeChatCompleted", "커피챗이 완료 처리되었습니다. 후기를 남겨보세요!");

        return ApplicationResponse.from(application);
    }

    @Transactional
    public ReviewResponse submitReview(Long reviewerId, Long applicationId, ReviewRequest request) {
        CoffeeChatApplication application = getApplicationOrThrow(applicationId);

        if (!application.isParticipant(reviewerId)) {
            throw new BusinessException(ErrorCode.NOT_APPLICATION_PARTICIPANT);
        }
        if (!application.isCompleted()) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_COMPLETED);
        }
        if (reviewRepository.existsByApplicationIdAndReviewerId(applicationId, reviewerId)) {
            throw new BusinessException(ErrorCode.ALREADY_REVIEWED);
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User reviewee = application.getApplicant().getId().equals(reviewerId)
                ? application.getReceiver()
                : application.getApplicant();

        CoffeeChatReview review = CoffeeChatReview.builder()
                .application(application)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        reviewRepository.save(review);
        return ReviewResponse.from(review);
    }

    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByRevieweeId(userId).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    private CoffeeChatApplication getApplicationOrThrow(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private void validateReceiver(CoffeeChatApplication application, Long userId) {
        if (!application.isReceiver(userId)) {
            throw new BusinessException(ErrorCode.NOT_APPLICATION_RECEIVER);
        }
    }

    private void validatePending(CoffeeChatApplication application) {
        if (!application.isPending()) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_PENDING);
        }
    }
}
