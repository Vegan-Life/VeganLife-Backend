package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.comment.service.CommentSearchService;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import com.konggogi.veganlife.post.service.dto.PostSimpleDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostSearchService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final PostLikeQueryService postLikeQueryService;
    private final CommentSearchService commentSearchService;
    private final PostMapper postMapper;

    public PostDetailsDto searchDetailsById(Long memberId, Long postId) {
        memberQueryService.search(memberId);
        Post post = postQueryService.searchWithMember(postId);

        boolean isLike = postLikeQueryService.searchPostLike(memberId, postId).isPresent();
        List<CommentDetailsDto> commentDetails = getAllCommentDetails(memberId, post);
        return postMapper.toPostDetailsDto(post, commentDetails, isLike);
    }

    public Page<PostSimpleDto> searchAllSimple(Pageable pageable) {
        return postQueryService
                .searchAll(pageable)
                .map(post -> postMapper.toPostSimpleDto(post, post.getImageUrls()));
    }

    public Page<PostSimpleDto> searchSimpleByKeyword(Pageable pageable, String keyword) {
        return postQueryService
                .searchByKeyword(pageable, keyword)
                .map(post -> postMapper.toPostSimpleDto(post, post.getImageUrls()));
    }

    private List<CommentDetailsDto> getAllCommentDetails(Long memberId, Post post) {
        return post.getComments().stream()
                .filter(comment -> comment.getParent().isEmpty())
                .map(
                        comment ->
                                commentSearchService.searchDetailsById(
                                        memberId, post.getId(), comment.getId()))
                .toList();
    }
}
