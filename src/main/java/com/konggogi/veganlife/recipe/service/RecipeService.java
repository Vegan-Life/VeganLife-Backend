package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import com.konggogi.veganlife.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final MemberQueryService memberQueryService;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    public void add(RecipeAddRequest request, Long memberId) {

        Recipe recipe = recipeMapper.toEntity(request, memberQueryService.search(memberId));
        recipeRepository.save(recipe);
    }
}
