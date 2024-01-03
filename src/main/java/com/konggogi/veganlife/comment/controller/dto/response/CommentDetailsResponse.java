package com.konggogi.veganlife.comment.controller.dto.response;


import com.konggogi.veganlife.comment.service.dto.SubCommentDetailsDto;
import java.util.List;

public record CommentDetailsResponse(
        Long id,
        String author,
        String content,
        boolean isLike,
        Integer likeCount,
        List<SubCommentDetailsDto> subComments) {}
