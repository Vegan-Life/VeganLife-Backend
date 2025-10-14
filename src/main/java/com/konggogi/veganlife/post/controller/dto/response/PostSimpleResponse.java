package com.konggogi.veganlife.post.controller.dto.response;


import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import java.time.LocalDateTime;

public record PostSimpleResponse(
        Long id, String title, String content, String imageUrl, LocalDateTime createdAt) {
    public static PostSimpleResponse from(Post post) {
        return new PostSimpleResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getThumbnail().map(PostImage::getImageUrl).orElse(null),
                post.getCreatedAt());
    }
}
