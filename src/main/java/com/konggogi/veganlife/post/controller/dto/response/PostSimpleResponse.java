package com.konggogi.veganlife.post.controller.dto.response;


import java.time.LocalDateTime;

public record PostSimpleResponse(
        Long id, String title, String content, String imageUrl, LocalDateTime createdAt) {}
