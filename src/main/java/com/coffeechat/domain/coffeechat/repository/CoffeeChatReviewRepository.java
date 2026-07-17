package com.coffeechat.domain.coffeechat.repository;

import com.coffeechat.domain.coffeechat.entity.CoffeeChatReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoffeeChatReviewRepository extends JpaRepository<CoffeeChatReview, Long> {

    boolean existsByApplicationIdAndReviewerId(Long applicationId, Long reviewerId);

    @Query("SELECT r FROM CoffeeChatReview r JOIN FETCH r.reviewer WHERE r.reviewee.id = :revieweeId ORDER BY r.createdAt DESC")
    List<CoffeeChatReview> findByRevieweeId(@Param("revieweeId") Long revieweeId);
}
