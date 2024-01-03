package com.konggogi.veganlife.comment.service.dto;

public record SubCommentDetailsDto(
        Long id, String author, String content, boolean isLike, Integer likeCount) {}
