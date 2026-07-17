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
    NOT_CHAT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CH002", "Not a member of this chat room"),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Post not found"),
    NOT_POST_AUTHOR(HttpStatus.FORBIDDEN, "P002", "Not the author of this post"),
    POST_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "P003", "Post is already closed"),

    // CoffeeChat Application
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APP001", "Application not found"),
    CANNOT_APPLY_OWN_POST(HttpStatus.BAD_REQUEST, "APP002", "Cannot apply to your own post"),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "APP003", "Already applied to this post"),
    APPLICATION_NOT_PENDING(HttpStatus.BAD_REQUEST, "APP004", "Application is not in pending status"),
    NOT_APPLICATION_RECEIVER(HttpStatus.FORBIDDEN, "APP005", "Not the receiver of this application"),
    NOT_APPLICATION_PARTICIPANT(HttpStatus.FORBIDDEN, "APP006", "Not a participant of this application"),
    APPLICATION_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "APP007", "Application is not in accepted status"),
    APPLICATION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "APP008", "Application is not completed"),
    ALREADY_REVIEWED(HttpStatus.CONFLICT, "APP009", "Already submitted a review for this application");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
