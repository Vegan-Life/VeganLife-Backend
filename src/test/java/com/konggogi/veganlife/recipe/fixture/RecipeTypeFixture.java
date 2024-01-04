package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.domain.RecipeType;

public enum RecipeTypeFixture {
    OVO(VegetarianType.OVO),
    LACTO(VegetarianType.LACTO),
    LACTO_OVO(VegetarianType.LACTO_OVO),
    VEGAN(VegetarianType.VEGAN),
    PESCO(VegetarianType.PESCO);

    private VegetarianType vegetarianType;

    RecipeTypeFixture(VegetarianType vegetarianType) {
        this.vegetarianType = vegetarianType;
    }

    public RecipeType get() {

        return new RecipeType(null, vegetarianType, null);
    }

    public RecipeType get(Long id) {

        return new RecipeType(id, vegetarianType, null);
    }
}
