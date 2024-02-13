package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.global.util.RandomUtils;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeSearchService {

    private static final int RECOMMEND_SIZE = 4;
    private static final int PAGE_SIZE = 1;

    private final MemberQueryService memberQueryService;
    private final RecipeQueryService recipeQueryService;
    private final RecipeLikeQueryService recipeLikeQueryService;

    private final RecipeMapper recipeMapper;

    public Page<RecipeResponse> searchAll(VegetarianType vegetarianType, Pageable pageable) {

        return recipeQueryService
                .searchAllByRecipeType(vegetarianType, pageable)
                .map(recipeMapper::toRecipeResponse);
    }

    public Page<RecipeResponse> searchAllByKeyword(String keyword, Pageable pageable) {

        return recipeQueryService
                .searchAllByKeyword(keyword, pageable)
                .map(recipeMapper::toRecipeResponse);
    }

    public RecipeDetailsResponse search(Long recipeId, Long memberId) {

        return recipeMapper.toRecipeDetailsResponse(
                recipeQueryService.search(recipeId), isLikedRecipe(recipeId, memberId));
    }

    public List<RecipeResponse> searchRecommended(Long memberId) {

        Member member = memberQueryService.search(memberId);

        return getRecommendRecipes(member).stream().map(recipeMapper::toRecipeResponse).toList();
    }

    // TODO: 랜덤 조회를 위해 5개의 쿼리가 발생, 성능 측정 필요
    /** 사용자의 VegetarainType에 해당하는 레시피 중 랜덤으로 4개를 추출한다. */
    private List<Recipe> getRecommendRecipes(Member member) {

        int totalElements = recipeQueryService.countAllRecipeType(member.getVegetarianType());

        return getRandomPageNumber(totalElements, member.getId()).stream()
                .map(
                        pageNumber ->
                                recipeQueryService.searchFirstElementByRecipeType(
                                        member.getVegetarianType(), pageNumber))
                .toList();
    }

    /** Pageable의 pageNumber를 랜덤으로 선택하고, pageSize는 1로 고정하여 반환한다. */
    private List<PageRequest> getRandomPageNumber(int totalElements, Long memberId) {

        long seed = RandomUtils.getSeedWithLocalDate(memberId, LocalDate.now());

        return RandomUtils.generateNotDuplicatedRandomNumbers(totalElements, RECOMMEND_SIZE, seed)
                .stream()
                .map(n -> PageRequest.of(n, PAGE_SIZE))
                .toList();
    }

    private boolean isLikedRecipe(Long recipeId, Long memberId) {

        return recipeLikeQueryService.searchByRecipeIdAndMemberId(recipeId, memberId).isPresent();
    }
}
