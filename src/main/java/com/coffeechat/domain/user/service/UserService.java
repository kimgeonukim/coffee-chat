package com.coffeechat.domain.user.service;

import com.coffeechat.domain.user.dto.UpdateProfileRequest;
import com.coffeechat.domain.user.dto.UserResponse;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

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
}
