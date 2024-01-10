package com.konggogi.veganlife.recipe.controller.dto.request;


import com.konggogi.veganlife.member.domain.VegetarianType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RecipeAddRequest(
        @NotNull @Size(max = 20) String name,
        @NotNull List<VegetarianType> recipeType,
        @NotNull List<String> imageUrls,
        @NotNull @Size(max = 100) List<String> ingredients,
        @NotNull @Size(max = 200) List<String> descriptions) {}
