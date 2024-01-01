package com.konggogi.veganlife.like.service;


import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.repository.CommentLikeRepository;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeQueryService {
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    public Optional<PostLike> searchPostLike(Long memberId, Long postId) {
        return postLikeRepository.findByMemberIdAndId(memberId, postId);
    }

    public Optional<CommentLike> searchCommentLike(Long memberId, Long commentId) {
        return commentLikeRepository.findByMemberIdAndId(memberId, commentId);
    }
}
