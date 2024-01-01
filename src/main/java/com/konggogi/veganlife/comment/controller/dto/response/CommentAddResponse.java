package com.konggogi.veganlife.comment.controller.dto.response;


import java.time.LocalDateTime;

public record CommentAddResponse(Long commentId, LocalDateTime createdAt) {}
