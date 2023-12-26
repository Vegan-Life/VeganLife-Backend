package com.konggogi.veganlife.meallog.fixture;


import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;

public enum MealImageFixture {
    DEFAULT("default.png");

    private String imageUrl;

    MealImageFixture(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MealImage get() {
        return MealImage.builder().imageUrl(imageUrl).build();
    }

    public MealImage get(Long id) {
        return MealImage.builder().id(id).imageUrl(imageUrl).build();
    }

    public MealImage getWithImageUrl(String imageUrl) {
        return MealImage.builder().imageUrl(imageUrl).build();
    }

    public MealImage getWithImageUrl(Long id, String imageUrl) {
        return MealImage.builder().imageUrl(imageUrl).build();
    }

    public MealImage getWithMealLog(MealLog mealLog) {
        return MealImage.builder().imageUrl(imageUrl).mealLog(mealLog).build();
    }
}
