package com.konggogi.veganlife.recipe.controller.dto.response;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import java.util.List;

public record RecipeResponse(
        Long id,
        String name,
        String thumbnailUrl,
        List<VegetarianType> recipeTypes,
        RecipeAuthorResponse author,
        boolean isLiked) {
    public static RecipeResponse from(Recipe recipe, boolean isLiked) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getThumbnail().map(RecipeImage::getImageUrl).orElse(null),
                recipe.getRecipeTypes().stream().map(RecipeType::getVegetarianType).toList(),
                RecipeAuthorResponse.from(recipe.getMember()),
                isLiked);
    }
}
