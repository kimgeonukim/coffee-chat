package com.coffeechat.domain.chat.controller;

import com.coffeechat.domain.chat.dto.ChatMessagePayload;
import com.coffeechat.domain.chat.dto.ChatMessageResponse;
import com.coffeechat.domain.chat.dto.ChatRoomResponse;
import com.coffeechat.domain.chat.dto.CreateChatRoomRequest;
import com.coffeechat.domain.chat.service.ChatService;
import com.coffeechat.global.response.ApiResponse;
import com.coffeechat.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "내 채팅방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<java.util.List<ChatRoomResponse>>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyRooms(userDetails.getUserId())));
    }

    @Operation(summary = "채팅방 생성")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateChatRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(chatService.createRoom(userDetails.getUserId(), request)));
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @Payload ChatMessagePayload payload) {
        chatService.sendMessage(userDetails.getUserId(), payload);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception e) {
        log.error("WebSocket error: {}", e.getMessage());
        return e.getMessage();
    }

    @Operation(summary = "채팅 메시지 목록 조회")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Slice<ChatMessageResponse>>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(roomId, page, size)));
    }
}
