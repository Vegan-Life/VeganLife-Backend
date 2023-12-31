package com.konggogi.veganlife.comment.controller.dto.request;


import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CommentAddRequest(Long parentId, @NotBlank @Length(max = 100) String content) {}
