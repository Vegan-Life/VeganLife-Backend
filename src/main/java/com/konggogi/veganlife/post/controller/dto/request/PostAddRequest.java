package com.konggogi.veganlife.post.controller.dto.request;


import java.util.List;

public record PostAddRequest(
        String title, String content, List<String> imageUrls, List<String> tags) {}
