package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.recipe.domain.RecipeIngredient;

public enum RecipeIngredientsFixture {
    DEFAULT("올리브유", 1, "tbsp");

    private String name;
    private Integer amount;
    private String unit;

    RecipeIngredientsFixture(String name, Integer amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public RecipeIngredient get() {

        return RecipeIngredient.builder().name(name).amount(amount).unit(unit).build();
    }

    public RecipeIngredient get(Long id) {

        return RecipeIngredient.builder().id(id).name(name).amount(amount).unit(unit).build();
    }
}
