package com.konggogi.veganlife.like.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.konggogi.veganlife.like.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.like.service.LikeService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LikeController.class)
class LikeControllerTest extends RestDocsTest {
    @MockBean LikeService likeService;

    @Test
    @DisplayName("게시글 좋아요 API")
    void addPostLikeTest() throws Exception {
        // given
        doNothing().when(likeService).addPostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "add-post-like",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("게시글 좋아요 API - 없는 회원 예외 발생")
    void addPostLikeNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(likeService)
                .addPostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-post-like-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 좋아요 API - 없는 게시글 예외 발생")
    void addPostLikeNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(likeService)
                .addPostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-post-like-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 좋아요 API - 이미 좋아요한 경우 예외 발생")
    void addPostLikeAlreadyTest() throws Exception {
        // given
        doThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_POST_LIKED))
                .when(likeService)
                .addPostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print()).andDo(document("add-post-like-already", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 API")
    void removePostLikeTest() throws Exception {
        // given
        doNothing().when(likeService).removePostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "remove-post-like",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 API - 없는 회원 예외 발생")
    void removePostLikeNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(likeService)
                .removePostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("remove-post-like-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 API - 없는 게시글 예외 발생")
    void removePostLikeNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(likeService)
                .removePostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("remove-post-like-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 API - 좋아요 되어 있지 않은 경우 예외 발생")
    void removePostLikeAlreadyTest() throws Exception {
        // given
        doThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_POST_UNLIKED))
                .when(likeService)
                .removePostLike(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/likes", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print()).andDo(document("remove-post-like-already", getDocumentResponse()));
    }
}
