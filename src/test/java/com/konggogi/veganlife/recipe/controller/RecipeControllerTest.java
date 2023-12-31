package com.konggogi.veganlife.recipe.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest extends RestDocsTest {

    @MockBean RecipeSearchService recipeSearchService;
    @Spy RecipeMapper recipeMapper = new RecipeMapperImpl();

    private final Member member = MemberFixture.DEFAULT_M.get();

    @Test
    @DisplayName("레시피 목록 조회 API")
    void getRecipeList() throws Exception {

        List<RecipeListResponse> recipe =
                List.of(
                        recipeMapper.toRecipeListResponse(
                                createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.LACTO.get())),
                        recipeMapper.toRecipeListResponse(
                                createRecipe(2L, "가지 탕수", RecipeTypeFixture.LACTO.get())),
                        recipeMapper.toRecipeListResponse(
                                createRecipe(3L, "통밀 츄러스", RecipeTypeFixture.LACTO.get())));
        Page<RecipeListResponse> response =
                PageableExecutionUtils.getPage(recipe, Pageable.ofSize(20), recipe::size);

        given(recipeSearchService.searchAll(any(VegetarianType.class), any(Pageable.class)))
                .willReturn(response);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/recipes")
                                .headers(authorizationHeader())
                                .queryParam("vegetarianType", VegetarianType.LACTO.name())
                                .queryParam("page", "0")
                                .queryParam("size", "20"));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(3))
                .andExpect(jsonPath("$.content.[0].id").value(1L))
                .andExpect(jsonPath("$.content.[0].name").value("표고버섯 탕수"))
                .andExpect(
                        jsonPath("$.content.[0].thumbnailUrl").value(recipe.get(0).thumbnailUrl()))
                .andExpect(jsonPath("$.content.[0].recipeTypes.size()").value(1))
                .andExpect(
                        jsonPath("$.content.[0].recipeTypes[0]")
                                .value(VegetarianType.LACTO.name()));

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recipe-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("vegetarianType")
                                                .description("레시피 채식 타입"),
                                        pageDesc(),
                                        sizeDesc())));
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
