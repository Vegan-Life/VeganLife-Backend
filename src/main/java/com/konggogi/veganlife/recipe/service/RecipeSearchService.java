package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeSearchService {

    private final MemberQueryService memberQueryService;
    private final RecipeQueryService recipeQueryService;
    private final RecipeMapper recipeMapper;

    public Page<RecipeResponse> searchAll(VegetarianType vegetarianType, Pageable pageable) {

        return recipeQueryService
                .searchAllByRecipeType(vegetarianType, pageable)
                .map(recipeMapper::toRecipeResponse);
    }

    public RecipeDetailsResponse search(Long id) {

        // TODO: 사용자가 좋아요를 눌렀는지 확인하는 로직 추가
        return recipeMapper.toRecipeDetailsResponse(recipeQueryService.search(id), false);
    }

    public List<RecipeResponse> searchRecommended(Long memberId) {

        Member member = memberQueryService.search(memberId);

        return recipeQueryService.searchAllRandomByRecipeType(member.getVegetarianType()).stream()
                .map(recipeMapper::toRecipeResponse)
                .toList();
    }
}
