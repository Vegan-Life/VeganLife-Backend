package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.repository.CommentLikeRepository;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.like.domain.mapper.LikeMapper;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.post.service.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentLikeService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CommentQueryService commentQueryService;
    private final CommentLikeQueryService commentLikeQueryService;
    private final CommentLikeNotifyService commentLikeNotifyService;
    private final CommentLikeRepository commentLikeRepository;
    private final LikeMapper likeMapper;

    public void addCommentLike(Long memberId, Long postId, Long commentId) {
        Member member = memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        Comment comment = commentQueryService.search(commentId);
        validateCommentLikeIsExist(memberId, commentId);
        CommentLike commentLike = likeMapper.toCommentLike(member, post);
        comment.addCommentLike(commentLike);
        commentLikeNotifyService.notifyAddCommentLikeIfNotAuthor(memberId, commentId);
    }

    public void removeCommentLike(Long memberId, Long postId, Long commentId) {
        memberQueryService.search(memberId);
        postQueryService.search(postId);
        Comment comment = commentQueryService.search(commentId);
        CommentLike commentLike = validateCommentLikeIsNotExist(memberId, commentId);
        comment.removeCommentLike(commentLike);
    }

    public void removeMemberFromCommentLike(Long memberId) {
        commentLikeRepository.setMemberToNull(memberId);
    }

    private void validateCommentLikeIsExist(Long memberId, Long commentId) {
        commentLikeQueryService
                .searchCommentLike(memberId, commentId)
                .ifPresent(
                        postLike -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_LIKED);
                        });
    }

    private CommentLike validateCommentLikeIsNotExist(Long memberId, Long commentId) {
        return commentLikeQueryService
                .searchCommentLike(memberId, commentId)
                .orElseThrow(
                        () -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_UNLIKED);
                        });
    }
}
