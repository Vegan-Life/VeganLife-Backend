package com.konggogi.veganlife.member.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import com.konggogi.veganlife.recipe.fixture.RecipeFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeTypeFixture;
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends RestDocsTest {
    @MockBean MemberService memberService;
    @MockBean MemberQueryService memberQueryService;
    @MockBean MealLogQueryService mealLogQueryService;
    @MockBean PostQueryService postQueryService;
    @MockBean RecipeSearchService recipeSearchService;

    @Test
    @DisplayName("회원 탈퇴 API")
    void removeMember() throws Exception {
        // when
        ResultActions perform =
                mockMvc.perform(delete("/api/v1/members").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "remove-member",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 탈퇴 API - 없는 회원 예외 발생")
    void removeNotMember() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(memberService)
                .removeMember(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(delete("/api/v1/members").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("remove-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("다른 사용자의 프로필을 조회한다.")
    void getMembersProfile() throws Exception {
        Long memberId = 1L;
        Member member = MemberFixture.DEFAULT_M.getWithId(memberId);
        given(memberQueryService.search(anyLong())).willReturn(member);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/{memberId}", memberId).headers(authorizationHeader()));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.vegetarianType").value(member.getVegetarianType().name()))
                .andExpect(jsonPath("$.profileImageUrl").value(member.getProfileImageUrl()));

        perform.andDo(print())
                .andDo(
                        document(
                                "get-member-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("memberId").description("조회할 사용자의 id"))));
    }

    @Test
    @DisplayName("다른 사용자의 식단 기록 목록을 조회한다.")
    void getMembersMealLogList() throws Exception {
        Long memberId = 1L;
        Member member = MemberFixture.DEFAULT_M.getWithId(memberId);
        given(memberQueryService.search(anyLong())).willReturn(member);
        List<MealData> mealData =
                List.of(
                        MealDataFixture.TOTAL_AMOUNT.get(1L, member),
                        MealDataFixture.AMOUNT_PER_SERVE.get(2L, member),
                        MealDataFixture.TOTAL_AMOUNT.get(3L, member));
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<String> imageUrls = List.of("image1.png", "image2.png", "image3.png");
        List<MealImage> mealImages =
                imageUrls.stream().map(MealImageFixture.DEFAULT::getWithImageUrl).toList();
        List<MealLog> mealLogs =
                List.of(MealLogFixture.BREAKFAST.get(1L, meals, mealImages, member));
        Page<MealLog> mealLogList = new PageImpl<>(mealLogs, Pageable.ofSize(10), mealLogs.size());
        given(mealLogQueryService.searchAllByMember(anyLong(), any(Pageable.class)))
                .willReturn(mealLogList);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/{memberId}/meal-log", memberId)
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10"));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(mealLogs.size()))
                .andExpect(jsonPath("$.content[0].id").value(mealLogs.get(0).getId()))
                .andExpect(
                        jsonPath("$.content[0].mealType")
                                .value(mealLogs.get(0).getMealType().name()))
                .andExpect(
                        jsonPath("$.content[0].thumbnailUrl")
                                .value(
                                        mealLogs.get(0)
                                                .getThumbnail()
                                                .map(MealImage::getImageUrl)
                                                .orElse(null)))
                .andExpect(
                        jsonPath("$.content[0].totalCalorie")
                                .value(mealLogs.get(0).getTotalCalorie()));

        perform.andDo(print())
                .andDo(
                        document(
                                "get-member-meal-log-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("memberId")
                                                .description("식단 목록을 조회할 사용자의 id")),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈"))));
    }

    @Test
    @DisplayName("다른 사용자의 피드 목록을 조회한다.")
    void getMembersPostList() throws Exception {
        Long memberId = 1L;
        Member member = MemberFixture.DEFAULT_M.getWithId(memberId);
        List<Post> posts = List.of(PostFixture.BAKERY.getWithDate(1L, member, LocalDate.now()));
        Page<Post> postList = new PageImpl<>(posts, Pageable.ofSize(10), posts.size());
        given(postQueryService.searchAll(anyLong(), any(Pageable.class))).willReturn(postList);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/{memberId}/posts", memberId)
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10"));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(posts.size()))
                .andExpect(jsonPath("$.content[0].id").value(posts.get(0).getId()))
                .andExpect(jsonPath("$.content[0].title").value(posts.get(0).getTitle()))
                .andExpect(jsonPath("$.content[0].content").value(posts.get(0).getContent()))
                .andExpect(
                        jsonPath("$.content[0].imageUrl")
                                .value(
                                        posts.get(0)
                                                .getThumbnail()
                                                .map(PostImage::getImageUrl)
                                                .orElse(null)));

        perform.andDo(print())
                .andDo(
                        document(
                                "get-member-post-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("memberId")
                                                .description("피드 목록을 조회할 사용자의 id")),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈"))));
    }

    @Test
    @DisplayName("다른 사용자의 레시피 목록을 조회한다.")
    void getMembersRecipeList() throws Exception {
        Long memberId = 1L;
        Member member = MemberFixture.DEFAULT_M.getWithId(memberId);
        List<RecipeType> recipeTypes = List.of(RecipeTypeFixture.LACTO.get());
        List<Recipe> recipes =
                List.of(RecipeFixture.DEFAULT.getSimpleWithRecipeTypes(1L, recipeTypes, member));
        Page<RecipeResponse> recipeList =
                new PageImpl<>(
                        recipes.stream().map((r) -> RecipeResponse.from(r, true)).toList(),
                        Pageable.ofSize(10),
                        recipes.size());
        given(recipeSearchService.searchAllByMemberId(anyLong(), any(Pageable.class)))
                .willReturn(recipeList);

        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/{memberId}/recipes", memberId)
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10"));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(recipes.size()))
                .andExpect(jsonPath("$.content[0].id").value(recipes.get(0).getId()))
                .andExpect(jsonPath("$.content[0].name").value(recipes.get(0).getName()))
                .andExpect(
                        jsonPath("$.content[0].thumbnailUrl")
                                .value(
                                        recipes.get(0)
                                                .getThumbnail()
                                                .map(RecipeImage::getImageUrl)
                                                .orElse(null)))
                .andExpect(
                        jsonPath("$.content[0].author.id")
                                .value(recipes.get(0).getMember().getId()))
                .andExpect(
                        jsonPath("$.content[0].author.nickname")
                                .value(recipes.get(0).getMember().getNickname()))
                .andExpect(
                        jsonPath("$.content[0].author.vegetarianType")
                                .value(recipes.get(0).getMember().getVegetarianType().name()))
                .andExpect(jsonPath("$.content[0].isLiked").value(true));

        perform.andDo(print())
                .andDo(
                        document(
                                "get-member-recipe-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("memberId")
                                                .description("레시피 목록을 조회할 사용자의 id")),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈"))));
    }
}
