package com.konggogi.veganlife.post.controller.dto.response;


import java.time.LocalDateTime;

public record PostAddResponse(Long postId, LocalDateTime createdAt) {}
