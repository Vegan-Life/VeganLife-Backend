package com.konggogi.veganlife.recipe.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
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
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import com.konggogi.veganlife.recipe.service.RecipeService;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest extends RestDocsTest {

    @MockBean RecipeSearchService recipeSearchService;
    @MockBean RecipeService recipeService;
    @Spy RecipeMapper recipeMapper = new RecipeMapperImpl();

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("레시피 목록 조회 API")
    void getRecipeListTest() throws Exception {

        List<RecipeResponse> recipe =
                List.of(
                        recipeMapper.toRecipeResponse(
                                createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.LACTO.get()), true),
                        recipeMapper.toRecipeResponse(
                                createRecipe(2L, "가지 탕수", RecipeTypeFixture.LACTO.get()), false),
                        recipeMapper.toRecipeResponse(
                                createRecipe(3L, "통밀 츄러스", RecipeTypeFixture.LACTO.get()), false));
        Page<RecipeResponse> response =
                PageableExecutionUtils.getPage(recipe, Pageable.ofSize(20), recipe::size);

        given(
                        recipeSearchService.searchAll(
                                any(VegetarianType.class), any(Pageable.class), anyLong()))
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
                        jsonPath("$.content.[0].recipeTypes[0]").value(VegetarianType.LACTO.name()))
                .andExpect(jsonPath("$.content.[0].author.id").value(member.getId()))
                .andExpect(jsonPath("$.content.[0].author.nickname").value(member.getNickname()))
                .andExpect(
                        jsonPath("$.content.[0].author.vegetarianType")
                                .value(member.getVegetarianType().name()))
                .andExpect(jsonPath("$.content.[0].isLiked").value(true));

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

    @Test
    @DisplayName("레시피 상세 조회 API")
    void getRecipeDetailsTest() throws Exception {

        Recipe recipe = createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.LACTO.get());
        RecipeDetailsResponse response = recipeMapper.toRecipeDetailsResponse(recipe, false);

        given(recipeSearchService.search(anyLong(), anyLong())).willReturn(response);

        ResultActions perform =
                mockMvc.perform(get("/api/v1/recipes/{id}", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("표고버섯 탕수"))
                .andExpect(jsonPath("$.recipeTypes.size()").value(1))
                .andExpect(jsonPath("$.recipeTypes[0]").value(VegetarianType.LACTO.name()))
                .andExpect(jsonPath("$.imageUrls.size()").value(3))
                .andExpect(jsonPath("$.ingredients.size()").value(3))
                .andExpect(jsonPath("$.descriptions.size()").value(3))
                .andExpect(jsonPath("$.author.id").value(member.getId()))
                .andExpect(jsonPath("$.author.nickname").value(member.getNickname()))
                .andExpect(
                        jsonPath("$.author.vegetarianType")
                                .value(member.getVegetarianType().name()))
                .andExpect(jsonPath("$.isLiked").value(false));

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recipe-details",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("레시피 id"))));
    }

    @Test
    @DisplayName("레시피 상세 조회 API - 레시피 not found 예외")
    void getRecipeDetailsNotFoundExceptionTest() throws Exception {

        given(recipeSearchService.search(anyLong(), anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));

        ResultActions perform =
                mockMvc.perform(get("/api/v1/recipes/{id}", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recipe-details-not-found",
                                getDocumentRequest(),
                                getDocumentResponse()));
    }

    @Test
    @DisplayName("레시피 등록 API")
    void addRecipeTest() throws Exception {

        RecipeAddRequest recipeAddRequest =
                new RecipeAddRequest(
                        "표고버섯 탕수",
                        List.of(VegetarianType.LACTO),
                        List.of("표고버섯 5개", "식용유", "시판 탕수육 소스"),
                        List.of("표고버섯을 먹기 좋은 크기로 자릅니다.", "표고버섯을 튀깁니다.", "탕수육 소스와 버무립니다."));
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(recipeAddRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/recipes")
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .headers(authorizationHeader()));

        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-add-recipe",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                requestParts(
                                        partWithName("request").description("레시피 추가 DTO"),
                                        partWithName("images").description("레시피 이미지 목록"))));
    }

    @Test
    @DisplayName("레시피 등록 API 예외 - Member Not Found")
    void addRecipeMemberNotFoundExceptionTest() throws Exception {

        RecipeAddRequest recipeAddRequest =
                new RecipeAddRequest(
                        "표고버섯 탕수",
                        List.of(VegetarianType.LACTO),
                        List.of("표고버섯 5개", "식용유", "시판 탕수육 소스"),
                        List.of("표고버섯을 먹기 좋은 크기로 자릅니다.", "표고버섯을 튀깁니다.", "탕수육 소스와 버무립니다."));
        MockMultipartFile request =
                new MockMultipartFile(
                        "request",
                        "request",
                        MediaType.APPLICATION_JSON_VALUE,
                        toJson(recipeAddRequest).getBytes());
        List<MockMultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1".getBytes()),
                        new MockMultipartFile(
                                "images",
                                "image2.png",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image2".getBytes()));

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .given(recipeService)
                .add(any(RecipeAddRequest.class), any(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        multipart("/api/v1/recipes")
                                .file(images.get(0))
                                .file(images.get(1))
                                .file(request)
                                .headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-add-recipe-member-not-found",
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 API")
    void getRecommendedRecipeTest() throws Exception {

        List<RecipeResponse> response =
                List.of(
                        recipeMapper.toRecipeResponse(
                                createRecipe(1L, "표고버섯 탕수", RecipeTypeFixture.LACTO.get()), false),
                        recipeMapper.toRecipeResponse(
                                createRecipe(2L, "가지 탕수", RecipeTypeFixture.LACTO.get()), true),
                        recipeMapper.toRecipeResponse(
                                createRecipe(3L, "통밀 츄러스", RecipeTypeFixture.LACTO.get()), true));

        given(recipeSearchService.searchRecommended(1L)).willReturn(response);

        ResultActions perform =
                mockMvc.perform(get("/api/v1/recipes/recommend").headers(authorizationHeader()));

        perform.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3));

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recommended-recipe",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 API 예외 - Member Not Found")
    void getRecommendedRecipeMemberNotFoundExceptionTest() throws Exception {

        given(recipeSearchService.searchRecommended(1L))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));

        ResultActions perform =
                mockMvc.perform(get("/api/v1/recipes/recommend").headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recommended-recipe-member-not-found",
                                getDocumentResponse()));
    }

    @Test
    @DisplayName("추천 레시피 목록 조회 API 예외 - Recipe Not Found")
    void getRecommendedRecipeRecipeNotFoundExceptionTest() throws Exception {

        given(recipeSearchService.searchRecommended(1L))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));

        ResultActions perform =
                mockMvc.perform(get("/api/v1/recipes/recommend").headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-get-recommended-recipe-recipe-not-found",
                                getDocumentResponse()));
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
                        RecipeIngredientFixture.DEFAULT.getWithName("표고버섯 5개"),
                        RecipeIngredientFixture.DEFAULT.getWithName("식용유 500ml"),
                        RecipeIngredientFixture.DEFAULT.getWithName("시판 탕수육 소스 100ml"));

        List<RecipeDescription> descriptions =
                List.of(
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(1, "표고버섯을 먹기 좋은 크기로 자릅니다."),
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(2, "표고버섯을 튀깁니다."),
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(3, "탕수육 소스에 버무립니다."));

        return RecipeFixture.DEFAULT.getWithName(
                id, name, recipeTypes, recipeImages, ingredients, descriptions, member);
    }
}
