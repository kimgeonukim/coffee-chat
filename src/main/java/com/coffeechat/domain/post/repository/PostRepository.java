package com.coffeechat.domain.post.repository;

import com.coffeechat.domain.post.entity.Category;
import com.coffeechat.domain.post.entity.Post;
import com.coffeechat.domain.post.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author ORDER BY p.createdAt DESC")
    Slice<Post> findAllWithAuthor(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.category = :category ORDER BY p.createdAt DESC")
    Slice<Post> findByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.category = :category AND p.subCategory = :subCategory ORDER BY p.createdAt DESC")
    Slice<Post> findByCategoryAndSubCategory(@Param("category") Category category, @Param("subCategory") SubCategory subCategory, Pageable pageable);
}
