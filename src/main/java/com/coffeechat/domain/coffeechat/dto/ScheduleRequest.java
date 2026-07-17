package com.coffeechat.domain.coffeechat.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleRequest(
        @NotNull(message = "일정 시간은 필수입니다")
        @Future(message = "일정은 미래 시간이어야 합니다")
        LocalDateTime scheduledAt
) {}
