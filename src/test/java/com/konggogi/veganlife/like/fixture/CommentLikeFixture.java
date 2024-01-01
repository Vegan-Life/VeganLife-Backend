package com.konggogi.veganlife.like.fixture;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;

public enum CommentLikeFixture {
    DEFAULT;

    CommentLikeFixture() {}

    public CommentLike get(Member member, Post post, Comment comment) {
        return CommentLike.builder().member(member).post(post).comment(comment).build();
    }

    public CommentLike getWithId(Long id, Member member, Post post, Comment comment) {
        return CommentLike.builder().id(id).member(member).post(post).comment(comment).build();
    }
}
