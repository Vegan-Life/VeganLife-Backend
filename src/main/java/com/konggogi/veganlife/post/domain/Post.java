package com.konggogi.veganlife.post.domain;


import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostImage> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(Long id, Member member, String title, String content) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public void addPostTag(PostTag postTag) {
        tags.add(postTag);
        postTag.setPost(this);
    }

    public void addPostImage(PostImage postImage) {
        imageUrls.add(postImage);
        postImage.setPost(this);
    }

    public void addPostLike(PostLike postLike) {
        likes.add(postLike);
        postLike.setPostAndMember(this, member);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removePostLike(PostLike postLike) {
        likes.remove(postLike);
    }
}
