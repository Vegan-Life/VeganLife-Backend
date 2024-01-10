package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;

    public Page<Recipe> searchAllByRecipeType(VegetarianType vegetarianType, Pageable pageable) {

        return recipeRepository.findAllByRecipeTypes(vegetarianType, pageable);
    }

    public Recipe search(Long id) {

        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));
    }
}
