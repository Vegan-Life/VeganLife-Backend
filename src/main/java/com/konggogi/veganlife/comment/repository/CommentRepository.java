package com.konggogi.veganlife.comment.repository;


import com.konggogi.veganlife.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select distinct c from Comment c join fetch c.member " + "where c.id = :commentId")
    Optional<Comment> findByIdFetchJoinMember(Long commentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Comment c set c.member = null where c.member.id = :memberId")
    void setMemberToNull(Long memberId);
}
