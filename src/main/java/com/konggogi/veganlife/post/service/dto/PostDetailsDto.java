package com.konggogi.veganlife.post.service.dto;


import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.post.domain.Post;
import java.util.List;

public record PostDetailsDto(
        Long id,
        Post post,
        boolean isLike,
        Integer likeCount,
        List<String> imageUrls,
        List<String> tags,
        List<CommentDetailsDto> comments) {}
