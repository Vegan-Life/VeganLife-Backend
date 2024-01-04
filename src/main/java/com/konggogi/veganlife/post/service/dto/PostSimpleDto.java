package com.konggogi.veganlife.post.service.dto;


import java.time.LocalDateTime;

public record PostSimpleDto(
        Long id, String title, String content, String imageUrl, LocalDateTime createdAt) {}
