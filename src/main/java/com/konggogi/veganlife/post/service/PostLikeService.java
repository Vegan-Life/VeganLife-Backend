package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.domain.mapper.LikeMapper;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final PostLikeQueryService likeQueryService;
    private final PostLikeRepository postLikeRepository;
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

    public void removeMemberFromPostLike(Long memberId) {
        postLikeRepository.setMemberToNull(memberId);
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
}
