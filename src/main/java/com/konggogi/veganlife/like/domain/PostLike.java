package com.konggogi.veganlife.like.domain;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public PostLike(Long id, Post post, Member member) {
        this.id = id;
        this.post = post;
        this.member = member;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
