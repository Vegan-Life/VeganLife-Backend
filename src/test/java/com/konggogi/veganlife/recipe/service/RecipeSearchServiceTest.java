package com.konggogi.veganlife.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeListResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapperImpl;
import com.konggogi.veganlife.recipe.fixture.RecipeDescriptionFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeImageFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeIngredientsFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeTypeFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@ExtendWith(MockitoExtension.class)
public class RecipeSearchServiceTest {

    @Mock RecipeQueryService recipeQueryService;
    @Spy RecipeMapper recipeMapper = new RecipeMapperImpl();
    @InjectMocks RecipeSearchService recipeSearchService;

    private final Member member = MemberFixture.DEFAULT_M.get();

    @Test
    @DisplayName("레시피 목록 조회 테스트 - response dto로 변환하여 반환한다.")
    void searchAllTest() {

        List<Recipe> recipes =
                List.of(
                        createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get()),
                        createRecipe(2L, "가지 탕수", RecipeTypeFixture.OVO.get()));
        Page<Recipe> result =
                PageableExecutionUtils.getPage(recipes, Pageable.ofSize(20), recipes::size);

        given(
                        recipeQueryService.searchAllByRecipeType(
                                any(VegetarianType.class), any(Pageable.class)))
                .willReturn(result);

        Page<RecipeListResponse> response =
                recipeSearchService.searchAll(VegetarianType.OVO, Pageable.ofSize(20));

        assertThat(response.getNumberOfElements()).isEqualTo(2);
        assertThat(response.getContent().get(0).thumbnailUrl())
                .isEqualTo(recipes.get(0).getThumbnailUrl());
        assertThat(response.getContent().get(1).thumbnailUrl())
                .isEqualTo(recipes.get(1).getThumbnailUrl());
        assertThat(response.getContent().get(0).recipeTypes())
                .containsAll(recipes.get(0).getRecipeTypes());
        assertThat(response.getContent().get(1).recipeTypes())
                .containsAll(recipes.get(1).getRecipeTypes());
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
                        RecipeIngredientsFixture.DEFAULT.get(),
                        RecipeIngredientsFixture.DEFAULT.get(),
                        RecipeIngredientsFixture.DEFAULT.get());

        List<RecipeDescription> descriptions =
                List.of(
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get());

        return RecipeFixture.DEFAULT.getWithName(
                id, name, recipeTypes, recipeImages, ingredients, descriptions, member);
    }
}
