package com.konggogi.veganlife.comment.service.dto;


import java.time.LocalDateTime;

public record SubCommentDetailsDto(
        Long id,
        String author,
        String content,
        boolean isLike,
        Integer likeCount,
        LocalDateTime createdAt) {}
