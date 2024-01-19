package com.konggogi.veganlife.recipe.controller.dto.response;


import com.konggogi.veganlife.member.domain.VegetarianType;
import java.util.List;

public record RecipeResponse(
        Long id, String name, String thumbnailUrl, List<VegetarianType> recipeTypes) {}
