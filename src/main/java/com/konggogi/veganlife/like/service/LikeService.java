package com.konggogi.veganlife.like.service;


import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.domain.mapper.LikeMapper;
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
    private final LikeMapper postLikeMapper;

    public void addPostLike(Long memberId, Long postId) {
        Member member = memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        postQueryService.validatePostLikeIsExist(memberId, postId);
        PostLike postLike = postLikeMapper.toEntity(member, post);
        post.addPostLike(postLike);
    }

    public void removePostLike(Long memberId, Long postId) {
        memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        PostLike postLike = postQueryService.validatePostLikeIsNotExist(memberId, postId);
        post.removePostLike(postLike);
    }
}
