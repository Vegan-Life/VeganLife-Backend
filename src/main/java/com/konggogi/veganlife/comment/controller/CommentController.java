package com.konggogi.veganlife.comment.controller;


import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.controller.dto.request.CommentModifyRequest;
import com.konggogi.veganlife.comment.controller.dto.response.CommentAddResponse;
import com.konggogi.veganlife.comment.controller.dto.response.CommentDetailsResponse;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.service.CommentSearchService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentSearchService commentSearchService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<CommentAddResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CommentAddRequest commentAddRequest) {
        Comment comment = commentService.add(userDetails.id(), postId, commentAddRequest);
        return ResponseEntity.ok(commentMapper.toCommentAddResponse(comment));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDetailsResponse> getCommentDetails(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentDetailsDto commentDetailsDto =
                commentSearchService.searchDetailsById(userDetails.id(), postId, commentId);
        return ResponseEntity.ok(commentMapper.toCommentDetailsResponse(commentDetailsDto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> modifyComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CommentModifyRequest commentModifyRequest) {
        commentService.modify(userDetails.id(), postId, commentId, commentModifyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> removeComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.remove(userDetails.id(), postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
