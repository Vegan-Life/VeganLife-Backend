package com.konggogi.veganlife.comment.service.dto;


import com.konggogi.veganlife.comment.domain.Comment;
import java.util.List;

public record CommentDetailsDto(
        Comment comment,
        boolean isLike,
        Integer likeCount,
        List<SubCommentDetailsDto> subComments) {}
