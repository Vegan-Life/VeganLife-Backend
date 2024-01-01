package com.konggogi.veganlife.comment.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.exception.IllegalCommentException;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends RestDocsTest {
    @MockBean CommentService commentService;
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final Post post = PostFixture.CHALLENGE.getPostAllInfoWithId(1L);
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
    private final Comment subComment =
            CommentFixture.DEFAULT.getSubCommentWithId(2L, member, post, comment);

    @Test
    @DisplayName("댓글 등록 API")
    void addTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(null, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willReturn(comment);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        perform.andDo(print())
                .andDo(
                        document(
                                "add-comment",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("댓글 등록 API - 없는 회원 예외 발생")
    void addNotFoundMemberTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(null, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-comment-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 등록 API - 없는 게시글 예외 발생")
    void addNotFoundPostTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(null, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("add-comment-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 등록 API - 대댓글")
    void addSubTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(1L, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willReturn(subComment);
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(subComment.getId()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        perform.andDo(print())
                .andDo(
                        document(
                                "add-sub-comment",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("댓글 등록 API - 없는 댓글 예외 발생")
    void addNotFoundCommentTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(1L, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("add-comment-not-found-comment", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 등록 API - 대댓글에 댓글을 등록하는 경우 예외 발생")
    void addIllegalCommentTest() throws Exception {
        // given
        CommentAddRequest request = new CommentAddRequest(2L, comment.getContent());
        given(commentService.add(anyLong(), anyLong(), any(CommentAddRequest.class)))
                .willThrow(new IllegalCommentException(ErrorCode.IS_NOT_PARENT_COMMENT));
        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/api/v1/posts/{postId}/comments", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isBadRequest());

        perform.andDo(print()).andDo(document("add-comment-illegal", getDocumentResponse()));
    }
}
