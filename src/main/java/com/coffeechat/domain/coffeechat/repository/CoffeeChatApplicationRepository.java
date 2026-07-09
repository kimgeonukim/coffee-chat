package com.coffeechat.domain.coffeechat.repository;

import com.coffeechat.domain.coffeechat.entity.CoffeeChatApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoffeeChatApplicationRepository extends JpaRepository<CoffeeChatApplication, Long> {

    boolean existsByPostIdAndApplicantId(Long postId, Long applicantId);

    @Query("SELECT a FROM CoffeeChatApplication a JOIN FETCH a.post JOIN FETCH a.applicant JOIN FETCH a.receiver WHERE a.receiver.id = :userId ORDER BY a.createdAt DESC")
    List<CoffeeChatApplication> findReceivedByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM CoffeeChatApplication a JOIN FETCH a.post JOIN FETCH a.applicant JOIN FETCH a.receiver WHERE a.applicant.id = :userId ORDER BY a.createdAt DESC")
    List<CoffeeChatApplication> findSentByUserId(@Param("userId") Long userId);
}
