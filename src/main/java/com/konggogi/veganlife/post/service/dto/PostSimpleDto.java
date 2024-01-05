package com.konggogi.veganlife.post.service.dto;


import com.konggogi.veganlife.post.domain.Post;
import java.util.List;

public record PostSimpleDto(Post post, List<String> imageUrls) {}
