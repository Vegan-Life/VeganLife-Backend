package com.konggogi.veganlife.post.controller.dto.response;


import com.konggogi.veganlife.comment.controller.dto.response.CommentDetailsResponse;
import com.konggogi.veganlife.member.domain.VegetarianType;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailsResponse(
        Long id,
        String author,
        VegetarianType vegetarianType,
        String profileImageUrl,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean isLike,
        Integer likeCount,
        Integer commentCount,
        List<String> imageUrls,
        List<String> tags,
        List<CommentDetailsResponse> comments) {}
