package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeLike;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeLikeMapper;
import com.konggogi.veganlife.recipe.repository.RecipeLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeLikeService {

    private final MemberQueryService memberQueryService;
    private final RecipeQueryService recipeQueryService;
    private final RecipeLikeQueryService recipeLikeQueryService;

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeLikeMapper recipeLikeMapper;

    public void add(Long recipeId, Long memberId) {

        Recipe recipe = recipeQueryService.search(recipeId);
        Member member = memberQueryService.search(memberId);

        validateRecipeLikeIsExist(recipe.getId(), member.getId());

        /** 쓰기 작업 전에 해당 레코드가 존재하는지 확인하는 로직만으로는 두 번 쓰기 작업을 시도하는 문제를 해결할 수 없다. */
        recipeLikeRepository.save(recipeLikeMapper.toEntity(recipe, member));
    }

    public void remove(Long recipeId, Long memberId) {

        Recipe recipe = recipeQueryService.search(recipeId);
        Member member = memberQueryService.search(memberId);

        RecipeLike recipeLike = validateRecipeLikeIsNotExist(recipe.getId(), member.getId());

        /** 쓰기 작업 전에 해당 레코드가 존재하는지 확인하는 로직만으로는 두 번 쓰기 작업을 시도하는 문제를 해결할 수 없다. */
        recipeLikeRepository.delete(recipeLike);
    }

    private void validateRecipeLikeIsExist(Long recipeId, Long memberId) {

        recipeLikeQueryService
                .searchByRecipeIdAndMemberId(recipeId, memberId)
                .ifPresent(
                        r -> {
                            throw new IllegalLikeStatusException(ErrorCode.ALREADY_RECIPE_LIKED);
                        });
    }

    private RecipeLike validateRecipeLikeIsNotExist(Long recipeId, Long memberId) {

        return recipeLikeQueryService
                .searchByRecipeIdAndMemberId(recipeId, memberId)
                .orElseThrow(
                        () -> new IllegalLikeStatusException(ErrorCode.ALREADY_RECIPE_UNLIKED));
    }
}
