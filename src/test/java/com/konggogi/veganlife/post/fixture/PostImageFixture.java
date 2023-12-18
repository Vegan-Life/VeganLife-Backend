package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;

public enum PostImageFixture {
    DEFAULT("https:/s3/images/image.png");
    private String imageUrl;

    PostImageFixture(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PostImage getPostImage() {
        return PostImage.builder().imageUrl(imageUrl).build();
    }

    public PostImage getPostImageWithIdAndPost(Long id, Post post) {
        return PostImage.builder().id(id).post(post).imageUrl(imageUrl).build();
    }

    public PostImage getPostImageWithPost(Post post) {
        return PostImage.builder().post(post).imageUrl(imageUrl).build();
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
