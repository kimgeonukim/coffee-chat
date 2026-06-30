package com.coffeechat.domain.chat.repository;

import com.coffeechat.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT DISTINCT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.id = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);
}
