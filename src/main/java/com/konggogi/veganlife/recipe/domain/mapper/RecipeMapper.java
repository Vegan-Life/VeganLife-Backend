package com.konggogi.veganlife.recipe.domain.mapper;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDescriptionDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeListResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(
            source = "recipe.thumbnailUrl",
            target = "thumbnailUrl",
            qualifiedByName = "recipeImageToImageUrl")
    @Mapping(
            source = "recipe.recipeTypes",
            target = "recipeTypes",
            qualifiedByName = "recipeTypeToVegetarianType")
    RecipeListResponse toRecipeListResponse(Recipe recipe);


    @Mapping(
            source = "recipe.recipeTypes",
            target = "recipeTypes",
            qualifiedByName = "recipeTypeToVegetarianType")
    @Mapping(source = "recipe.recipeImages", target = "imageUrls", qualifiedByName = "recipeImageToImageUrl")
    @Mapping(source = "recipe.ingredients", target = "ingredients", qualifiedByName = "recipeIngredientsToString")
    RecipeDetailsResponse toRecipeDetailsResponse(Recipe recipe, boolean isLiked);

    @Named("recipeImageToImageUrl")
    static String recipeImageToImageUrl(RecipeImage recipeImage) {

        return recipeImage.getImageUrl();
    }

    @Named("recipeTypeToVegetarianType")
    static VegetarianType recipeTypeToVegetarianType(RecipeType recipeType) {

        return recipeType.getVegetarianType();
    }

    @Named("recipeIngredientsToString")
    static String recipeIngredientsToString(RecipeIngredient ingredient) {

        return ingredient.getName();
    }
}
