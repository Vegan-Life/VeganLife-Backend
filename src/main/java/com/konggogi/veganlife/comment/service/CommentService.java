package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.exception.IllegalCommentException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.service.PostQueryService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CommentQueryService commentQueryService;
    private final CommentMapper commentMapper;

    public Comment add(Long memberId, Long postId, CommentAddRequest commentAddRequest) {
        Member member = memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        Comment comment = commentMapper.toEntity(member, commentAddRequest);

        getParentCommentId(commentAddRequest)
                .ifPresent(
                        parentId -> {
                            Comment parentComment = commentQueryService.search(parentId);
                            parentComment
                                    .getParent()
                                    .ifPresent(
                                            parent -> {
                                                throw new IllegalCommentException(
                                                        ErrorCode.IS_NOT_PARENT_COMMENT);
                                            });
                            parentComment.addSubComment(comment);
                        });
        post.addComment(comment);
        return comment;
    }

    private Optional<Long> getParentCommentId(CommentAddRequest commentAddRequest) {
        return Optional.ofNullable(commentAddRequest.parentId());
    }
}
