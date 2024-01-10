package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.recipe.domain.RecipeIngredient;

public enum RecipeIngredientFixture {
    DEFAULT("올리브유 1 tbsp");

    private String name;

    RecipeIngredientFixture(String name) {
        this.name = name;
    }

    public RecipeIngredient get() {

        return RecipeIngredient.builder().name(name).build();
    }

    public RecipeIngredient get(Long id) {

        return RecipeIngredient.builder().id(id).name(name).build();
    }

    public RecipeIngredient getWithName(String name) {

        return RecipeIngredient.builder().name(name).build();
    }
}
