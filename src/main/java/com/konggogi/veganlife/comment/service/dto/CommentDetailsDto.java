package com.konggogi.veganlife.comment.service.dto;


import com.konggogi.veganlife.like.domain.CommentLike;
import java.util.List;

public record CommentDetailsDto(
        Long id,
        String author,
        String content,
        boolean isLike,
        List<SubCommentDetailsDto> subComments,
        List<CommentLike> likes) {
    public int countLikes() {
        return likes.size();
    }
}
