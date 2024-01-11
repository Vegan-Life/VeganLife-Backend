package com.konggogi.veganlife.recipe.controller.dto.request;


import com.konggogi.veganlife.global.annotation.StringElementLength;
import com.konggogi.veganlife.member.domain.VegetarianType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RecipeAddRequest(
        @NotNull @Size(max = 20) String name,
        @NotNull @Size(min = 1, max = 2) List<VegetarianType> recipeType,
        @NotNull @Size(max = 5) List<String> imageUrls,
        @NotNull @Size(max = 10) @StringElementLength(max = 100) List<String> ingredients,
        @NotNull @Size(max = 10) @StringElementLength(max = 200) List<String> descriptions) {}
