package com.konggogi.veganlife.comment.controller;


import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts/{postId}/comments/{commentId}/likes")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping
    public ResponseEntity<Void> addCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentLikeService.addCommentLike(userDetails.id(), postId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentLikeService.removeCommentLike(userDetails.id(), postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
