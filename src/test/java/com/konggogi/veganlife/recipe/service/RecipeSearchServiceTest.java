package com.konggogi.veganlife.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
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
import com.konggogi.veganlife.recipe.fixture.RecipeIngredientFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeTypeFixture;
import java.util.List;
import java.util.Objects;
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

    @Mock MemberQueryService memberQueryService;
    @Mock RecipeQueryService recipeQueryService;
    @Spy RecipeMapper recipeMapper = new RecipeMapperImpl();
    @InjectMocks RecipeSearchService recipeSearchService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

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

        Page<RecipeResponse> response =
                recipeSearchService.searchAll(VegetarianType.OVO, Pageable.ofSize(20));

        assertThat(response.getNumberOfElements()).isEqualTo(2);
        assertThat(response.getContent().get(0).thumbnailUrl())
                .isEqualTo(recipes.get(0).getThumbnail().getImageUrl());
        assertThat(response.getContent().get(1).thumbnailUrl())
                .isEqualTo(recipes.get(1).getThumbnail().getImageUrl());
        assertThat(response.getContent().get(0).recipeTypes())
                .containsAll(
                        recipes.get(0).getRecipeTypes().stream()
                                .map(RecipeType::getVegetarianType)
                                .toList());
        assertThat(response.getContent().get(1).recipeTypes())
                .containsAll(
                        recipes.get(0).getRecipeTypes().stream()
                                .map(RecipeType::getVegetarianType)
                                .toList());
    }

    @Test
    @DisplayName("레시피 상세 조회 테스트")
    void searchTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.LACTO.get());

        given(recipeQueryService.search(1L)).willReturn(recipe);

        RecipeDetailsResponse response = recipeSearchService.search(1L);

        assertThat(response.isLiked()).isEqualTo(false);
        assertThat(response.name()).isEqualTo("표고버섯 탕수");
        assertThat(response.recipeTypes()).hasSize(1);
        assertThat(response.recipeTypes().get(0)).isEqualTo(VegetarianType.LACTO);
        assertThat(response.imageUrls())
                .containsAll(
                        recipe.getRecipeImages().stream().map(RecipeImage::getImageUrl).toList());
        assertThat(response.ingredients())
                .containsAll(
                        recipe.getIngredients().stream().map(RecipeIngredient::getName).toList());
        assertThat(response.descriptions()).allMatch(Objects::nonNull);
    }

    @Test
    @DisplayName("레시피 상세 조회 테스트 - 존재하지 않는 레시피")
    void searchNotFoundExceptionTest() {

        given(recipeQueryService.search(1L))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));

        assertThatThrownBy(() -> recipeSearchService.search(1L))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_RECIPE.getDescription());
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 테스트")
    void searchRecommendedTest() {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.OVO.get());

        given(memberQueryService.search(anyLong())).willReturn(member);
        given(recipeQueryService.countAllRecipeType(any(VegetarianType.class))).willReturn(1);
        given(
                        recipeQueryService.searchFirstElementByRecipeType(
                                any(VegetarianType.class), any(Pageable.class)))
                .willReturn(recipe);

        List<RecipeResponse> response = recipeSearchService.searchRecommended(anyLong());

        assertThat(response).hasSize(1);
        assertThat(response.get(0).id()).isEqualTo(1L);
        assertThat(response.get(0).name()).isEqualTo("표고버섯 탕수");
        assertThat(response.get(0).recipeTypes()).hasSameElementsAs(List.of(VegetarianType.OVO));
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 테스트 - 존재하지 않는 사용자")
    void searchRecommendedMemberNotFoundExceptionTest() {

        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));

        assertThatThrownBy(() -> recipeSearchService.searchRecommended(1L))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getDescription());
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 테스트 - 존재하지 않는 레시피")
    void searchRecommendedRecipeNotFoundExceptionTest() {

        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));

        assertThatThrownBy(() -> recipeSearchService.searchRecommended(1L))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_RECIPE.getDescription());
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
