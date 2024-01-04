package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.post.repository.PostLikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeQueryService {
    private final PostLikeRepository postLikeRepository;

    public Optional<PostLike> searchPostLike(Long memberId, Long postId) {
        return postLikeRepository.findByMemberIdAndPostId(memberId, postId);
    }
}
