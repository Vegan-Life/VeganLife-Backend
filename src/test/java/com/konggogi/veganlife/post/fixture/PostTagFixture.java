package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;

public enum PostTagFixture {
    DEFAULT;

    public PostTag get(Post post, Tag tag) {
        PostTag postTag = PostTag.builder().post(post).tag(tag).build();
        post.addPostTag(postTag);
        return postTag;
    }

    public PostTag getWithId(Long id, Post post, Tag tag) {
        PostTag postTag = PostTag.builder().id(id).post(post).tag(tag).build();
        post.addPostTag(postTag);
        return postTag;
    }
}
