package com.konggogi.veganlife.like.repository;


import com.konggogi.veganlife.like.domain.CommentLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberIdAndCommentId(Long memberId, Long commentId);
}
