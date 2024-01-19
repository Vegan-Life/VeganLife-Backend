package com.konggogi.veganlife.recipe.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.recipe.service.RecipeLikeService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(RecipeLikeController.class)
public class RecipeLikeControllerTest extends RestDocsTest {

    @MockBean RecipeLikeService recipeLikeService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("레시피 좋아요 API")
    void addRecipeLikeTest() throws Exception {

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-like-add",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("레시피 id"))));
    }

    @Test
    @DisplayName("레시피 좋아요 취소 API")
    void removeRecipeLikeTest() throws Exception {

        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "recipe-like-remove",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("id").description("레시피 id"))));
    }

    @Test
    @DisplayName("레시피 좋아요/좋아요 취소 API 예외 - Recipe Not Found")
    void addRecipeLikeRecipeNotFoundExceptionTest() throws Exception {

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE))
                .given(recipeLikeService)
                .add(anyLong(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("recipe-like-add-recipe-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("레시피 좋아요/좋아요 취소 API 예외 - Member Not Found")
    void addRecipeLikeMemberNotFoundExceptionTest() throws Exception {

        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .given(recipeLikeService)
                .add(anyLong(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("recipe-like-add-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("레시피 좋아요 API 예외 - 중복 좋아요")
    void addRecipeLikeDuplicateExceptionTest() throws Exception {

        willThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_RECIPE_LIKED))
                .given(recipeLikeService)
                .add(anyLong(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("recipe-like-add-member-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("레시피 좋아요 취소 API 예외 - 중복 좋아요 취소")
    void removeRecipeLikeDuplicateExceptionTest() throws Exception {

        willThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_RECIPE_UNLIKED))
                .given(recipeLikeService)
                .remove(anyLong(), anyLong());

        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/recipes/{id}/likes", 1L).headers(authorizationHeader()));

        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("recipe-like-remove-member-not-found", getDocumentResponse()));
    }
}
