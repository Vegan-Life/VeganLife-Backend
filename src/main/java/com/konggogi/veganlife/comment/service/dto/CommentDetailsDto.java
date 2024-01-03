package com.konggogi.veganlife.comment.service.dto;


import java.util.List;

public record CommentDetailsDto(
        Long id,
        String author,
        String content,
        boolean isLike,
        Integer likeCount,
        List<SubCommentDetailsDto> subComments) {}
