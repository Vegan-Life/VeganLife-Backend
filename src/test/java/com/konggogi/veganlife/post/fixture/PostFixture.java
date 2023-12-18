package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;

public enum PostFixture {
    DEFAULT("신사동 비건 베이커리 맛집 추천드려요", "오늘은 신사동 비건 베이커리 맛집 TOP5를 소개해드릴게요! 우선 첫 번째는...");

    private Long id;
    private final String title;
    private final String content;

    PostFixture(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post getPost() {
        Post post = createBasePost();
        Tag tag = TagFixture.DEFAULT.getTag();
        post.addPostTag(tag);
        return post;
    }

    public Post getPostWithTagName(String tagName) {
        Post post = createBasePost();
        Tag tag = TagFixture.DEFAULT.getTagWithName(tagName);
        post.addPostTag(tag);
        return post;
    }

    public Post getOnlyTitleAndContent() {
        return Post.builder().title(title).content(content).build();
    }

    private Post createBasePost() {
        Post post = Post.builder().title(title).content(content).build();
        String url = PostImageFixture.DEFAULT.getImageUrl();
        post.addPostImage(url);
        return post;
    }
}
