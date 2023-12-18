package com.konggogi.veganlife.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {
    @Test
    @DisplayName("게시글에 태그 추가")
    void addPostTagTest() {
        // given
        Post post = PostFixture.BAKERY.getPost();
        Tag tag = TagFixture.STORE.getTag();
        // when
        post.addPostTag(tag);
        // then
        assertThat(post.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("게시글에 이미지 추가")
    void addPostImageTest() {
        // given
        Post post = PostFixture.BAKERY.getPost();
        String url = "imageUrl.jpg";
        // when
        post.addPostImage(url);
        // then
        assertThat(post.getImageUrls()).hasSize(1);
    }
}
