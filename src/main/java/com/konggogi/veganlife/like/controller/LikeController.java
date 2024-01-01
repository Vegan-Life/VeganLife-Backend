package com.konggogi.veganlife.like.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class LikeController {
    private final LikeService likeService;

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

    @PostMapping("/{postId}/comments/{commentId}/likes")
    public ResponseEntity<Void> addCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.addCommentLike(userDetails.id(), postId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
