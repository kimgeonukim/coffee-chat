package com.coffeechat.domain.post.service;

import com.coffeechat.domain.post.dto.CreatePostRequest;
import com.coffeechat.domain.post.dto.PostResponse;
import com.coffeechat.domain.post.dto.UpdatePostRequest;
import com.coffeechat.domain.post.entity.Category;
import com.coffeechat.domain.post.entity.Post;
import com.coffeechat.domain.post.entity.SubCategory;
import com.coffeechat.domain.post.repository.PostRepository;
import com.coffeechat.domain.user.entity.User;
import com.coffeechat.domain.user.repository.UserRepository;
import com.coffeechat.global.exception.BusinessException;
import com.coffeechat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse create(Long userId, CreatePostRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.subCategory() != null && !request.subCategory().belongsTo(request.category())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "서브카테고리가 카테고리에 속하지 않습니다");
        }

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .subCategory(request.subCategory())
                .author(author)
                .build();

        postRepository.save(post);
        return PostResponse.from(post);
    }

    public Slice<PostResponse> getPosts(Category category, SubCategory subCategory, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Slice<Post> posts;

        if (category != null && subCategory != null) {
            posts = postRepository.findByCategoryAndSubCategory(category, subCategory, pageRequest);
        } else if (category != null) {
            posts = postRepository.findByCategory(category, pageRequest);
        } else {
            posts = postRepository.findAllWithAuthor(pageRequest);
        }

        return posts.map(PostResponse::from);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long userId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR);
        }

        if (request.subCategory() != null && !request.subCategory().belongsTo(request.category())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "서브카테고리가 카테고리에 속하지 않습니다");
        }

        post.update(request.title(), request.content(), request.category(), request.subCategory());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR);
        }

        postRepository.delete(post);
    }

    @Transactional
    public void close(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isAuthor(userId)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR);
        }

        if (post.isClosed()) {
            throw new BusinessException(ErrorCode.POST_ALREADY_CLOSED);
        }

        post.close();
    }
}
