package com.konggogi.veganlife.post.fixture;


import com.konggogi.veganlife.post.domain.Tag;

public enum TagFixture {
    DEFAULT("#태그"),
    STORE("#맛집"),
    CHALLENGE("#챌린지");
    private String name;

    TagFixture(String name) {
        this.name = name;
    }

    public Tag getTag() {
        return Tag.builder().name(name).build();
    }

    public Tag getTagWithId(Long id) {
        return Tag.builder().id(id).name(name).build();
    }

    public Tag getTagWithIdAndName(Long id, String name) {
        return Tag.builder().id(id).name(name).build();
    }

    public Tag getTagWithName(String name) {
        return Tag.builder().name(name).build();
    }

    public String getName() {
        return name;
    }
}
