package com.konggogi.veganlife.like.service;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.service.CommentLikeNotifyService;
import com.konggogi.veganlife.comment.service.CommentQueryService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.domain.mapper.LikeMapper;
import com.konggogi.veganlife.like.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.like.repository.CommentLikeRepository;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.service.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CommentQueryService commentQueryService;
    private final LikeQueryService likeQueryService;
    private final CommentLikeNotifyService commentLikeNotifyService;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final LikeMapper likeMapper;

    public void addPostLike(Long memberId, Long postId) {
        Member member = memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        validatePostLikeIsExist(memberId, postId);
        PostLike postLike = likeMapper.toPostLike(member);
        post.addPostLike(postLike);
    }

    public void removePostLike(Long memberId, Long postId) {
        memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        PostLike postLike = validatePostLikeIsNotExist(memberId, postId);
        post.removePostLike(postLike);
    }

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

    public void removeMemberFromLike(Long memberId) {
        postLikeRepository.setMemberToNull(memberId);
        commentLikeRepository.setMemberToNull(memberId);
    }

    private void validatePostLikeIsExist(Long memberId, Long postId) {
        likeQueryService
                .searchPostLike(memberId, postId)
                .ifPresent(
                        postLike -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_POST_LIKED);
                        });
    }

    private PostLike validatePostLikeIsNotExist(Long memberId, Long postId) {
        return likeQueryService
                .searchPostLike(memberId, postId)
                .orElseThrow(
                        () -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_POST_UNLIKED);
                        });
    }

    private void validateCommentLikeIsExist(Long memberId, Long commentId) {
        likeQueryService
                .searchCommentLike(memberId, commentId)
                .ifPresent(
                        postLike -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_LIKED);
                        });
    }

    private CommentLike validateCommentLikeIsNotExist(Long memberId, Long commentId) {
        return likeQueryService
                .searchCommentLike(memberId, commentId)
                .orElseThrow(
                        () -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_UNLIKED);
                        });
    }
}
