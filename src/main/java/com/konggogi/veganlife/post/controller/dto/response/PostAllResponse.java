package com.konggogi.veganlife.post.controller.dto.response;


import com.konggogi.veganlife.post.service.dto.PostSimpleDto;
import java.util.List;
import org.springframework.data.domain.Page;

public record PostAllResponse(List<String> topTags, Page<PostSimpleDto> posts) {}
