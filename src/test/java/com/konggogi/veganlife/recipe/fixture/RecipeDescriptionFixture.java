package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.recipe.domain.RecipeDescription;

public enum RecipeDescriptionFixture {
    DEFAULT(1, "표고버섯을 먹기 좋은 크기로 자릅니다.");

    private Integer sequence;
    private String description;

    RecipeDescriptionFixture(Integer sequence, String description) {
        this.sequence = sequence;
        this.description = description;
    }

    public RecipeDescription get() {

        return RecipeDescription.builder().sequence(sequence).description(description).build();
    }

    public RecipeDescription get(Long id) {

        return RecipeDescription.builder()
                .id(id)
                .sequence(sequence)
                .description(description)
                .build();
    }
}
