package com.konggogi.veganlife.recipe.domain.mapper;


import com.konggogi.veganlife.recipe.controller.dto.response.RecipeListResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "thumbnailUrl", expression = "java(recipe.getThumbnailUrl())")
    @Mapping(target = "recipeTypes", expression = "java(recipe.getRecipeTypes())")
    RecipeListResponse toRecipeListResponse(Recipe recipe);
}
