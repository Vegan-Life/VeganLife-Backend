package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeListResponse;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeSearchService {

    private final RecipeQueryService recipeQueryService;
    private final RecipeMapper recipeMapper;

    public Page<RecipeListResponse> searchAll(VegetarianType vegetarianType, Pageable pageable) {

        return recipeQueryService
                .searchAllByRecipeType(vegetarianType, pageable)
                .map(recipeMapper::toRecipeListResponse);
    }
}
