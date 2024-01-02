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
        Post post = PostFixture.BAKERY.get();
        Tag tag = TagFixture.STORE.getTag();
        PostTag postTag = PostTag.builder().tag(tag).build();
        // when
        post.addPostTag(postTag);
        // then
        assertThat(post.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("게시글에 이미지 추가")
    void addPostImageTest() {
        // given
        Post post = PostFixture.BAKERY.get();
        String url = "imageUrl.jpg";
        PostImage postImage = PostImage.builder().imageUrl(url).build();
        // when
        post.addPostImage(postImage);
        // then
        assertThat(post.getImageUrls()).hasSize(1);
    }
}
