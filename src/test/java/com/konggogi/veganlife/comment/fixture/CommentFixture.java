package com.konggogi.veganlife.comment.fixture;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.springframework.util.ReflectionUtils;

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
        Comment comment =
                Comment.builder().id(id).member(member).post(post).content(content).build();
        return setCreatedAt(comment);
    }

    public Comment getSubComment(Member member, Post post, Comment comment) {
        return Comment.builder()
                .member(member)
                .post(post)
                .parentComment(comment)
                .content(content)
                .build();
    }

    public Comment getSubCommentWithId(Long id, Member member, Post post, Comment parentComment) {
        Comment comment =
                Comment.builder()
                        .id(id)
                        .member(member)
                        .post(post)
                        .parentComment(parentComment)
                        .content(content)
                        .build();
        return setCreatedAt(comment);
    }

    private Comment setCreatedAt(Comment comment) {
        Field createdAtField = ReflectionUtils.findField(Comment.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAtField);
        ReflectionUtils.setField(createdAtField, comment, LocalDateTime.now());
        return comment;
    }
}
