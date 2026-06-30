package com.coffeechat.domain.auth.service;

import com.coffeechat.domain.auth.dto.LoginRequest;
import com.coffeechat.domain.auth.dto.SignUpRequest;
import com.coffeechat.domain.auth.dto.TokenResponse;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.entity.UserRole;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import com.coffeechat.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String REFRESH_TOKEN_KEY = "refresh:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            redisTemplate.opsForValue().set(
                    REFRESH_TOKEN_KEY + request.email(),
                    refreshToken,
                    7,
                    TimeUnit.DAYS
            );

            return TokenResponse.of(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public void logout(String email) {
        redisTemplate.delete(REFRESH_TOKEN_KEY + email);
    }
}
