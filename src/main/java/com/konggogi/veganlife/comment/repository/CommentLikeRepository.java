package com.konggogi.veganlife.comment.repository;


import com.konggogi.veganlife.comment.domain.CommentLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberIdAndCommentId(Long memberId, Long commentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CommentLike c SET c.member = NULL WHERE c.member.id = :memberId")
    void setMemberToNull(Long memberId);
}
