package com.konggogi.veganlife.post.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostImageFixture;
import com.konggogi.veganlife.post.service.LikeService;
import com.konggogi.veganlife.post.service.PostService;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PostController.class)
class PostControllerTest extends RestDocsTest {
    @MockBean PostService postService;
    @MockBean LikeService likeService;

    @Test
    @DisplayName("게시글 등록 API")
    void addPostTest() throws Exception {
        // given
        Post post = PostFixture.BAKERY.getPostAllInfoWithId(1L);
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("#맛집");
        PostAddRequest request =
                new PostAddRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        given(postService.add(anyLong(), any(PostAddRequest.class))).willReturn(post);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        perform.andDo(print())
                .andDo(
                        document(
                                "add-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("게시글 등록 API - 없는 회원 예외 발생")
    void addPostNotMemberTest() throws Exception {
        // given
        Post post = PostFixture.BAKERY.getPostAllInfoWithId(1L);
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("#맛집");
        PostAddRequest request =
                new PostAddRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        given(postService.add(anyLong(), any(PostAddRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("add-post-not-found", getDocumentResponse()));
    }

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
        doThrow(new IllegalLikeStatusException(ErrorCode.ALREADY_LIKED))
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
}
