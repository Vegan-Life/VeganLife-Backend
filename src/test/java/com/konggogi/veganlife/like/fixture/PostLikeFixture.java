package com.konggogi.veganlife.like.fixture;


import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;

public enum PostLikeFixture {
    DEFAULT;

    PostLikeFixture() {}

    public PostLike get(Member member, Post post) {
        return PostLike.builder().member(member).post(post).build();
    }

    public PostLike getWithId(Long id, Member member, Post post) {
        return PostLike.builder().id(id).member(member).post(post).build();
    }
}
