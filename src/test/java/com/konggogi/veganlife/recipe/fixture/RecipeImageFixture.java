package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.recipe.domain.RecipeImage;

public enum RecipeImageFixture {
    DEFAULT("/image1.png");

    private String imageUrl;

    RecipeImageFixture(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RecipeImage get() {

        return new RecipeImage(null, imageUrl, null);
    }

    public RecipeImage get(Long id) {

        return new RecipeImage(id, imageUrl, null);
    }
}
