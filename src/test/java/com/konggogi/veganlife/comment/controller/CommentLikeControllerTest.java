package com.konggogi.veganlife.comment.controller;

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

import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest extends RestDocsTest {
    @MockBean CommentLikeService commentLikeService;

    @Test
    @DisplayName("댓글 좋아요 API")
    void addCommentLikeTest() throws Exception {
        // given
        doNothing().when(commentLikeService).addCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "add-comment-like",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 번호"),
                                        parameterWithName("commentId").description("댓글 번호"))));
    }

    @Test
    @DisplayName("댓글 좋아요 API - 없는 회원 예외 발생")
    void addCommentLikeNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(commentLikeService)
                .addCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-comment-like-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 API - 없는 게시글 예외 발생")
    void addCommentLikeNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(commentLikeService)
                .addCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-comment-like-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 API - 없는 댓글 예외 발생")
    void addCommentLikeNotFoundCommentTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT))
                .when(commentLikeService)
                .addCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-comment-like-not-found-comment", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 API - 이미 좋아요한 경우 예외 발생")
    void addCommentLikeAlreadyLikeTest() throws Exception {
        // given
        doThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_LIKED))
                .when(commentLikeService)
                .addCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print()).andDo(document("add-comment-like-already", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 API")
    void removeCommentLikeTest() throws Exception {
        // given
        doNothing().when(commentLikeService).removeCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "remove-comment-like",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 번호"),
                                        parameterWithName("commentId").description("댓글 번호"))));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 API - 없는 회원 예외 발생")
    void removeCommentLikeNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(commentLikeService)
                .removeCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("remove-comment-like-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 API - 없는 게시글 예외 발생")
    void removeCommentLikeNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(commentLikeService)
                .removeCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("remove-comment-like-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 API - 없는 댓글 예외 발생")
    void removeCommentLikeNotFoundCommentTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT))
                .when(commentLikeService)
                .removeCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("remove-comment-like-not-found-comment", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 API - 이미 좋아요한 경우 예외 발생")
    void removeCommentLikeAlreadyLikeTest() throws Exception {
        // given
        doThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_COMMENT_UNLIKED))
                .when(commentLikeService)
                .removeCommentLike(anyLong(), anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("remove-comment-like-already", getDocumentResponse()));
    }
}
