package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;
import com.konggogi.veganlife.post.repository.PostLikeRepository;
import com.konggogi.veganlife.post.repository.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public Post search(Long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
    }

    public Optional<PostLike> searchPostLike(Long memberId, Long postId) {
        return postLikeRepository.findByMemberIdAndId(memberId, postId);
    }
}
