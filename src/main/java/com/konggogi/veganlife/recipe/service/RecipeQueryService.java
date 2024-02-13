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

    /** 사용자의 VegetarianType으로 조회한 결과 중 첫번째 요소를 반환한다. */
    public Recipe searchFirstElementByRecipeType(VegetarianType vegetarianType, Pageable pageable) {

        return searchAllByRecipeType(vegetarianType, pageable).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));
    }

    public Recipe search(Long id) {

        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));
    }

    public int countAllRecipeType(VegetarianType vegetarianType) {

        return recipeRepository.countByRecipeType(vegetarianType);
    }

    public Page<Recipe> searchAllByKeyword(String keyword, Pageable pageable) {

        return recipeRepository.findAllByKeyword(keyword, pageable);
    }
}
