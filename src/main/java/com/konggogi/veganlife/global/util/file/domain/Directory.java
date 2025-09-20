package com.konggogi.veganlife.global.util.file.domain;

public enum Directory {
    COMMUNITY("community/"),
    LIFE_CHECK("lifecheck/"),
    PROFILE("profile/"),
    RECIPE("recipe/");

    private final String name;

    Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
