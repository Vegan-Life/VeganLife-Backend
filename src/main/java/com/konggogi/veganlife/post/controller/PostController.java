package com.konggogi.veganlife.post.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.controller.dto.response.PopularTagsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostDetailsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostLikeService;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.post.service.PostSearchService;
import com.konggogi.veganlife.post.service.PostService;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostSearchService postSearchService;
    private final PostLikeService postLikeService;
    private final PostQueryService postQueryService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostAddResponse> addPost(
            @RequestBody @Valid PostAddRequest postAddRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.add(userDetails.id(), postAddRequest);
        return ResponseEntity.ok(postMapper.toPostAddResponse(post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailsResponse> getPost(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDetailsDto postDetailsDto =
                postSearchService.searchDetailsById(userDetails.id(), postId);
        return ResponseEntity.ok(postMapper.toPostDetailsResponse(postDetailsDto));
    }

    @GetMapping()
    public ResponseEntity<Page<PostSimpleResponse>> getAllPost(Pageable pageable) {
        Page<PostSimpleResponse> postSimpleResponsePage =
                postSearchService.searchAllSimple(pageable).map(postMapper::toPostSimpleResponse);
        return ResponseEntity.ok(postSimpleResponsePage);
    }

    @GetMapping("/tags")
    public ResponseEntity<PopularTagsResponse> getPopularTags() {
        List<Tag> topTags = postQueryService.searchPopularTags();
        return ResponseEntity.ok(postMapper.toPopularTagsResponse(topTags));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> addPostLike(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postLikeService.addPostLike(userDetails.id(), postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> removePostLike(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postLikeService.removePostLike(userDetails.id(), postId);
        return ResponseEntity.noContent().build();
    }
}
