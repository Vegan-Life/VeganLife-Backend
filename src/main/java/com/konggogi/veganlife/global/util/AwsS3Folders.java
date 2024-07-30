package com.konggogi.veganlife.global.util;

public enum AwsS3Folders {
    COMMUNITY("community/"),
    LIFE_CHECK("lifecheck/"),
    PROFILE("profile/"),
    RECIPE("recipe/");

    private final String name;

    AwsS3Folders(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
