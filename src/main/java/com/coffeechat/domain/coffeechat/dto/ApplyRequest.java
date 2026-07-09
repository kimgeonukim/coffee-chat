package com.coffeechat.domain.coffeechat.dto;

import jakarta.validation.constraints.Size;

public record ApplyRequest(
        @Size(max = 500, message = "신청 메시지는 500자 이하여야 합니다")
        String message
) {}
