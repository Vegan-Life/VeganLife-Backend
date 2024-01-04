package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.comment.service.dto.SubCommentDetailsDto;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.service.PostQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentSearchService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CommentQueryService commentQueryService;
    private final CommentLikeQueryService commentLikeQueryService;
    private final CommentMapper commentMapper;

    public CommentDetailsDto searchDetailsById(Long memberId, Long postId, Long commentId) {
        memberQueryService.search(memberId);
        postQueryService.search(postId);
        Comment foundComment = commentQueryService.searchWithMember(commentId);

        boolean isLike = commentLikeQueryService.isCommentLike(memberId, commentId);
        List<SubCommentDetailsDto> subComments = getAllSubCommentDetails(memberId, foundComment);
        return commentMapper.toCommentDetailsDto(foundComment, subComments, isLike);
    }

    private SubCommentDetailsDto toSubCommentDetails(Long memberId, Comment subComment) {
        boolean isLike = commentLikeQueryService.isCommentLike(memberId, subComment.getId());
        return commentMapper.toSubCommentDetailsDto(subComment, isLike);
    }

    private List<SubCommentDetailsDto> getAllSubCommentDetails(Long memberId, Comment comment) {
        return comment.getSubComments().stream()
                .map(subComment -> toSubCommentDetails(memberId, subComment))
                .toList();
    }
}
