package com.konggogi.veganlife.recipe.controller.dto.response;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;

public record RecipeAuthorResponse(Long id, String nickname, VegetarianType vegetarianType) {
    public static RecipeAuthorResponse from(Member member) {
        return new RecipeAuthorResponse(
                member.getId(), member.getNickname(), member.getVegetarianType());
    }
}
