package com.konggogi.veganlife.recipe.domain.mapper;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeListResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(
            target = "thumbnailUrl",
            expression = "java(RecipeMapper.recipeImageToImageUrl(recipe.getThumbnailUrl()))")
    @Mapping(
            source = "recipe.recipeTypes",
            target = "recipeTypes",
            qualifiedByName = "recipeTypeToVegetarianType")
    RecipeListResponse toRecipeListResponse(Recipe recipe);

    static String recipeImageToImageUrl(RecipeImage recipeImage) {

        return recipeImage.getImageUrl();
    }

    @Named("recipeTypeToVegetarianType")
    static VegetarianType recipeTypeToVegetarianType(RecipeType recipeType) {

        return recipeType.getVegetarianType();
    }
}
