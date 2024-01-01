package com.konggogi.veganlife.comment.fixture;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;

public enum CommentFixture {
    DEFAULT("좋은 생각인 것 같습니다!");
    private final String content;

    CommentFixture(String content) {
        this.content = content;
    }

    public Comment getTopComment(Member member, Post post) {
        return Comment.builder().member(member).post(post).content(content).build();
    }

    public Comment getTopCommentWithId(Long id, Member member, Post post) {
        return Comment.builder().id(id).member(member).post(post).content(content).build();
    }

    public Comment getSubComment(Member member, Post post, Comment comment) {
        return Comment.builder()
                .member(member)
                .post(post)
                .parentComment(comment)
                .content(content)
                .build();
    }

    public Comment getSubCommentWithId(Long id, Member member, Post post, Comment comment) {
        return Comment.builder()
                .id(id)
                .member(member)
                .post(post)
                .parentComment(comment)
                .content(content)
                .build();
    }
}
