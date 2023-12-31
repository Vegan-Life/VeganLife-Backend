package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.repository.CommentRepository;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentRepository commentRepository;

    public Comment search(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT));
    }
}
