package com.konggogi.veganlife.comment.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // If parentComment is null, parent comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> subComments = new ArrayList<>();

    @OneToMany(mappedBy = "comment", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CommentLike> likes = new ArrayList<>();

    @Builder
    public Comment(Long id, String content, Member member, Post post, Comment parentComment) {
        this.id = id;
        this.content = content;
        this.member = member;
        this.post = post;
        this.parentComment = parentComment;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setParentComment(Comment comment) {
        this.parentComment = comment;
    }

    public void addSubComment(Comment comment) {
        subComments.add(comment);
        comment.setParentComment(this);
    }

    public Optional<Comment> getParent() {
        return Optional.ofNullable(parentComment);
    }

    public void addCommentLike(CommentLike commentLike) {
        likes.add(commentLike);
        commentLike.setComment(this);
    }

    public void removeCommentLike(CommentLike commentLike) {
        likes.remove(commentLike);
    }

    public int countLikes() {
        return likes.size();
    }
}
