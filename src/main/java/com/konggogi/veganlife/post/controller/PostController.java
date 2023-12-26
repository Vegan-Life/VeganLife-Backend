package com.konggogi.veganlife.post.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.LikeService;
import com.konggogi.veganlife.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostAddResponse> addPost(
            @RequestBody @Valid PostAddRequest postAddRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.add(userDetails.id(), postAddRequest);
        return ResponseEntity.ok(postMapper.toPostAddResponse(post));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> addPostLike(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.addPostLike(userDetails.id(), postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> removePostLike(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.removePostLike(userDetails.id(), postId);
        return ResponseEntity.noContent().build();
    }
}
