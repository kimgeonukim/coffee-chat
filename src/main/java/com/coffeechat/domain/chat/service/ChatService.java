package com.coffeechat.domain.chat.service;

import com.coffeechat.domain.chat.dto.ChatMessagePayload;
import com.coffeechat.domain.chat.dto.ChatMessageResponse;
import com.coffeechat.domain.chat.dto.ChatRoomResponse;
import com.coffeechat.domain.chat.dto.CreateChatRoomRequest;
import com.coffeechat.domain.chat.entity.ChatMessage;
import com.coffeechat.domain.chat.entity.ChatRoom;
import com.coffeechat.domain.chat.entity.ChatRoomMember;
import com.coffeechat.domain.chat.repository.ChatMessageRepository;
import com.coffeechat.domain.chat.repository.ChatRoomMemberRepository;
import com.coffeechat.domain.chat.repository.ChatRoomRepository;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomRepository.findAllByUserId(userId).stream()
                .map(ChatRoomResponse::from)
                .toList();
    }

    @Transactional
    public ChatRoomResponse createRoom(Long creatorId, CreateChatRoomRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.name())
                .type(request.type())
                .build();
        chatRoomRepository.save(chatRoom);

        List<Long> memberIds = new ArrayList<>();
        memberIds.add(creatorId);
        if (request.memberIds() != null) {
            request.memberIds().stream()
                    .filter(id -> !id.equals(creatorId))
                    .forEach(memberIds::add);
        }

        List<User> members = userRepository.findAllById(memberIds);
        members.forEach(user -> chatRoomMemberRepository.save(
                ChatRoomMember.builder().chatRoom(chatRoom).user(user).build()
        ));

        return ChatRoomResponse.from(chatRoom);
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long senderId, ChatMessagePayload payload) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(payload.roomId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isMember = chatRoom.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(senderId));
        if (!isMember) {
            throw new BusinessException(ErrorCode.NOT_CHAT_ROOM_MEMBER);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(payload.content())
                .messageType(payload.messageType())
                .build();

        chatMessageRepository.save(message);

        ChatMessageResponse response = ChatMessageResponse.from(message);
        messagingTemplate.convertAndSend("/topic/chat/" + payload.roomId(), response);
        return response;
    }

    public Slice<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        return chatMessageRepository
                .findByChatRoomId(roomId, PageRequest.of(page, size))
                .map(ChatMessageResponse::from);
    }
}
