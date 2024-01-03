package com.konggogi.veganlife.comment.service.dto;


import com.konggogi.veganlife.member.domain.Member;
import java.util.List;

public record CommentDetailsDto(
        Long id,
        Member author,
        String content,
        boolean isLike,
        Integer likeCount,
        List<SubCommentDetailsDto> subComments) {}
