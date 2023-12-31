package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;

public enum PostLikeFixture {
    DEFAULT;

    PostLikeFixture() {}

    public PostLike get(Member member, Post post) {
        PostLike postLike = PostLike.builder().member(member).post(post).build();
        post.addPostLike(postLike);
        return postLike;
    }

    public PostLike getWithId(Long id, Member member, Post post) {
        PostLike postLike = PostLike.builder().id(id).member(member).post(post).build();
        post.addPostLike(postLike);
        return postLike;
    }
}
