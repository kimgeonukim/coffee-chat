package com.coffeechat.domain.user.service;

import com.coffeechat.domain.user.dto.UpdateProfileRequest;
import com.coffeechat.domain.user.dto.UserResponse;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import com.coffeechat.global.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    public List<UserResponse> searchUsers(String keyword) {
        return userRepository.searchByKeyword(keyword).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateProfile(request.nickname(), request.bio());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String oldUrl = user.getProfileImageUrl();
        String newUrl = fileStorageService.store(file); // 실패 시 기존 이미지 보존
        user.updateProfileImage(newUrl);
        if (oldUrl != null) {
            fileStorageService.delete(oldUrl); // 업로드 성공 후 구 이미지 삭제
        }
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getProfileImageUrl() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "프로필 이미지가 없습니다");
        }
        fileStorageService.delete(user.getProfileImageUrl());
        user.updateProfileImage(null);
        return UserResponse.from(user);
    }
}
