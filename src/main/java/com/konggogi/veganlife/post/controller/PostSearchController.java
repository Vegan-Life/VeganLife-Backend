package com.konggogi.veganlife.post.controller;


import com.konggogi.veganlife.post.controller.dto.response.PopularTagsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.post.service.PostSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostSearchController {
    private final PostQueryService postQueryService;
    private final PostSearchService postSearchService;
    private final PostMapper postMapper;

    @GetMapping("/search")
    public ResponseEntity<Page<PostSimpleResponse>> getPostListByKeyword(
            @RequestParam String keyword, Pageable pageable) {
        Page<PostSimpleResponse> postSimpleResponsePage =
                postSearchService
                        .searchSimpleByKeyword(pageable, keyword)
                        .map(postMapper::toPostSimpleResponse);
        return ResponseEntity.ok(postSimpleResponsePage);
    }

    @GetMapping("/tags")
    public ResponseEntity<PopularTagsResponse> getPopularTags() {
        List<Tag> topTags = postQueryService.searchPopularTags();
        return ResponseEntity.ok(postMapper.toPopularTagsResponse(topTags));
    }

    @GetMapping("/complete/search")
    public ResponseEntity<List<String>> getAutoCompleteSuggestion(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(postQueryService.suggestByKeyword(keyword, size));
    }
}
