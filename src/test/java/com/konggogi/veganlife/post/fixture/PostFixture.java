package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.Post;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.springframework.util.ReflectionUtils;

public enum PostFixture {
    BAKERY("신사동 비건 베이커리 맛집 추천", "오늘은 신사동 비건 베이커리 맛집 TOP5를 소개해드릴게요! 우선 첫 번째는...", "#맛집"),
    CHALLENGE("락토 챌린지 함께해요", "락토 모여라~ 30일간 진행되는 챌린지 함께하실 분 구해요. 자세한 일정은...", "#챌린지");
    private final String title;
    private final String content;
    private final String tag;

    PostFixture(String title, String content, String tag) {
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

    public Post getPost() {
        return Post.builder().title(title).content(content).build();
    }

    public Post getPostAllInfoWithId(Long postId) {
        Post post = Post.builder().id(postId).title(title).content(content).build();
        post.addPostImage(PostImageFixture.DEFAULT.getImageUrl());
        post.addPostTag(TagFixture.DEFAULT.getTagWithName(tag));
        return setCreatedAtOfPost(post);
    }

    private Post setCreatedAtOfPost(Post post) {
        Field createdAtField = ReflectionUtils.findField(Post.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAtField);
        ReflectionUtils.setField(createdAtField, post, LocalDateTime.now());
        return post;
    }
}
