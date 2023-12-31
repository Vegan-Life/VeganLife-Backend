package com.konggogi.veganlife.comment.repository;


import com.konggogi.veganlife.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long commentId);
}
