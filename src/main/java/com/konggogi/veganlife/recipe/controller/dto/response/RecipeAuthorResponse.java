package com.konggogi.veganlife.recipe.controller.dto.response;


import com.konggogi.veganlife.member.domain.VegetarianType;

public record RecipeAuthorResponse(Long id, String nickname, VegetarianType vegetarianType) {}
