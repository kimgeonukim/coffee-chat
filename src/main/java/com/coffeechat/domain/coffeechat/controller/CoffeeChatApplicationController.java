package com.coffeechat.domain.coffeechat.controller;

import com.coffeechat.domain.coffeechat.dto.ApplicationResponse;
import com.coffeechat.domain.coffeechat.dto.ApplyRequest;
import com.coffeechat.domain.coffeechat.dto.ReviewRequest;
import com.coffeechat.domain.coffeechat.dto.ReviewResponse;
import com.coffeechat.domain.coffeechat.dto.ScheduleRequest;
import com.coffeechat.domain.coffeechat.service.CoffeeChatApplicationService;
import com.coffeechat.global.response.ApiResponse;
import com.coffeechat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CoffeeChat", description = "커피챗 신청/매칭 API")
@RestController
@RequiredArgsConstructor
public class CoffeeChatApplicationController {

    private final CoffeeChatApplicationService applicationService;

    @Operation(summary = "커피챗 신청")
    @PostMapping("/api/posts/{postId}/applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody(required = false) ApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(applicationService.apply(userDetails.getUserId(), postId, request)));
    }

    @Operation(summary = "받은 신청 목록")
    @GetMapping("/api/applications/received")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getReceived(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getReceivedApplications(userDetails.getUserId())));
    }

    @Operation(summary = "보낸 신청 목록")
    @GetMapping("/api/applications/sent")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getSent(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getSentApplications(userDetails.getUserId())));
    }

    @Operation(summary = "커피챗 신청 수락 (채팅방 자동 생성)")
    @PatchMapping("/api/applications/{applicationId}/accept")
    public ResponseEntity<ApiResponse<ApplicationResponse>> accept(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.accept(userDetails.getUserId(), applicationId)));
    }

    @Operation(summary = "커피챗 신청 거절")
    @PatchMapping("/api/applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse<ApplicationResponse>> reject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.reject(userDetails.getUserId(), applicationId)));
    }

    @Operation(summary = "커피챗 일정 제안/수정 (신청자·수락자 모두 가능)")
    @PatchMapping("/api/applications/{applicationId}/schedule")
    public ResponseEntity<ApiResponse<ApplicationResponse>> schedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.schedule(userDetails.getUserId(), applicationId, request)));
    }

    @Operation(summary = "커피챗 완료 처리 (신청자·수락자 모두 가능)")
    @PatchMapping("/api/applications/{applicationId}/complete")
    public ResponseEntity<ApiResponse<ApplicationResponse>> complete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.complete(userDetails.getUserId(), applicationId)));
    }

    @Operation(summary = "후기/평점 작성 (완료 후 1인 1회)")
    @PostMapping("/api/applications/{applicationId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(applicationService.submitReview(userDetails.getUserId(), applicationId, request)));
    }

    @Operation(summary = "특정 유저의 받은 후기 목록")
    @GetMapping("/api/users/{userId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getReviewsByUser(userId)));
    }
}
