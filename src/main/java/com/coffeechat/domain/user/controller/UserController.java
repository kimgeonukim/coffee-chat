package com.coffeechat.domain.user.controller;

import com.coffeechat.domain.user.dto.UpdateProfileRequest;
import com.coffeechat.domain.user.dto.UserResponse;
import com.coffeechat.domain.user.service.UserService;
import com.coffeechat.global.response.ApiResponse;
import com.coffeechat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userDetails.getUserId())));
    }

    @Operation(summary = "프로필 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(userDetails.getUserId(), request)));
    }

    @Operation(summary = "유저 검색 (닉네임/이메일)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(userService.searchUsers(keyword)));
    }
}
