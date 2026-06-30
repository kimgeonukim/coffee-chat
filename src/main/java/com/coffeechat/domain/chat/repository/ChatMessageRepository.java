package com.coffeechat.domain.chat.repository;

import com.coffeechat.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom.id = :roomId ORDER BY m.createdAt DESC")
    Slice<ChatMessage> findByChatRoomId(@Param("roomId") Long roomId, Pageable pageable);
}
