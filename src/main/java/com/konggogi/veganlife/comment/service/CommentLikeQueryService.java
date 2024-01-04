package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.comment.repository.CommentLikeRepository;
import com.konggogi.veganlife.like.domain.CommentLike;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeQueryService {
    private final CommentLikeRepository commentLikeRepository;

    public Optional<CommentLike> searchCommentLike(Long memberId, Long commentId) {
        return commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId);
    }

    public boolean isCommentLike(Long memberId, Long commentId) {
        return searchCommentLike(memberId, commentId).isPresent();
    }
}
