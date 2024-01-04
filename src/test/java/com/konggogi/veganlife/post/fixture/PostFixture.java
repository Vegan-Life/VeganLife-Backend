package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.springframework.util.ReflectionUtils;

public enum PostFixture {
    BAKERY("신사동 비건 베이커리 맛집 추천", "오늘은 신사동 비건 베이커리 맛집 TOP5를 소개해드릴게요! 우선 첫 번째는...", "#맛집"),
    CHALLENGE("락토 챌린지 함께해요", "락토 모여라~ 30일간 진행되는 챌린지 함께하실 분 구해요. 자세한 일정은...", "#챌린지");
    private final String title;
    private final String content;
    private final String tagName;

    PostFixture(String title, String content, String tagName) {
        this.title = title;
        this.content = content;
        this.tagName = tagName;
    }

    public Post get() {
        return Post.builder().title(title).content(content).build();
    }

    public Post get(Member member) {
        return Post.builder().member(member).title(title).content(content).build();
    }

    public Post getWithId(Long id, Member member) {
        Post post = Post.builder().id(id).title(title).content(content).member(member).build();
        Tag tag = TagFixture.DEFAULT.getTagWithName(tagName);
        PostTag postTag = PostTag.builder().tag(tag).build();
        post.addPostTag(postTag);
        post.addPostImage(PostImageFixture.DEFAULT.getPostImage());
        return setCreatedAt(post);
    }

    private Post setCreatedAt(Post post) {
        Field createdAtField = ReflectionUtils.findField(Post.class, "createdAt");
        ReflectionUtils.makeAccessible(createdAtField);
        ReflectionUtils.setField(createdAtField, post, LocalDateTime.now());
        return post;
    }
}
