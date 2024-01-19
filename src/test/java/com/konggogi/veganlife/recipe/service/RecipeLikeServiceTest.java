package com.konggogi.veganlife.recipe.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeLike;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeLikeMapper;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeLikeMapperImpl;
import com.konggogi.veganlife.recipe.fixture.RecipeDescriptionFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeImageFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeIngredientFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeTypeFixture;
import com.konggogi.veganlife.recipe.repository.RecipeLikeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecipeLikeServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock RecipeQueryService recipeQueryService;
    @Mock RecipeLikeQueryService recipeLikeQueryService;
    @Mock RecipeLikeRepository recipeLikeRepository;
    @Spy RecipeLikeMapper recipeLikeMapper = new RecipeLikeMapperImpl();
    @InjectMocks RecipeLikeService recipeLikeService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("레시피 좋아요 테스트")
    void addTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get());

        given(recipeQueryService.search(1L)).willReturn(recipe);
        given(memberQueryService.search(1L)).willReturn(member);
        given(recipeLikeQueryService.searchByRecipeIdAndMemberId(1L, 1L))
                .willReturn(Optional.empty());

        recipeLikeService.add(1L, 1L);

        then(recipeQueryService).should(times(1)).search(1L);
        then(memberQueryService).should(times(1)).search(1L);
        then(recipeLikeQueryService).should(times(1)).searchByRecipeIdAndMemberId(1L, 1L);
        then(recipeLikeRepository).should(times(1)).save(any(RecipeLike.class));
    }

    @Test
    @DisplayName("레시피 좋아요 테스트 - 중복 좋아요")
    void addDuplicationTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get());
        RecipeLike recipeLike = new RecipeLike(null, recipe, member);

        given(recipeQueryService.search(1L)).willReturn(recipe);
        given(memberQueryService.search(1L)).willReturn(member);
        given(recipeLikeQueryService.searchByRecipeIdAndMemberId(1L, 1L))
                .willReturn(Optional.of(recipeLike));

        assertThatThrownBy(() -> recipeLikeService.add(1L, 1L))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessage(ErrorCode.ALREADY_RECIPE_LIKED.getDescription());
    }

    @Test
    @DisplayName("레시피 좋아요 취소 테스트")
    void removeTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get());
        RecipeLike recipeLike = new RecipeLike(null, recipe, member);

        given(recipeQueryService.search(1L)).willReturn(recipe);
        given(memberQueryService.search(1L)).willReturn(member);
        given(recipeLikeQueryService.searchByRecipeIdAndMemberId(1L, 1L))
                .willReturn(Optional.of(recipeLike));

        recipeLikeService.remove(1L, 1L);

        then(recipeQueryService).should(times(1)).search(1L);
        then(memberQueryService).should(times(1)).search(1L);
        then(recipeLikeQueryService).should(times(1)).searchByRecipeIdAndMemberId(1L, 1L);
        then(recipeLikeRepository).should(times(1)).delete(any(RecipeLike.class));
    }

    @Test
    @DisplayName("레시피 좋아요 취소 테스트 - 중복 좋아요 취소")
    void removeDuplicationTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get());

        given(recipeQueryService.search(1L)).willReturn(recipe);
        given(memberQueryService.search(1L)).willReturn(member);
        given(recipeLikeQueryService.searchByRecipeIdAndMemberId(1L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> recipeLikeService.remove(1L, 1L))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessage(ErrorCode.ALREADY_RECIPE_UNLIKED.getDescription());
    }

    private Recipe createRecipe(Long id, String name, RecipeType recipeType) {

        List<RecipeType> recipeTypes = List.of(recipeType);

        List<RecipeImage> recipeImages =
                List.of(
                        RecipeImageFixture.DEFAULT.get(),
                        RecipeImageFixture.DEFAULT.get(),
                        RecipeImageFixture.DEFAULT.get());

        List<RecipeIngredient> ingredients =
                List.of(
                        RecipeIngredientFixture.DEFAULT.get(),
                        RecipeIngredientFixture.DEFAULT.get(),
                        RecipeIngredientFixture.DEFAULT.get());

        List<RecipeDescription> descriptions =
                List.of(
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get());

        return RecipeFixture.DEFAULT.getWithName(
                id, name, recipeTypes, recipeImages, ingredients, descriptions, member);
    }
}
