package com.coffeechat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "Resource not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "Internal server error"),

    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Expired token"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A003", "Unauthorized"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A004", "Invalid email or password"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "Email already exists"),

    // Chat
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CH001", "Chat room not found"),
    NOT_CHAT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CH002", "Not a member of this chat room");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
