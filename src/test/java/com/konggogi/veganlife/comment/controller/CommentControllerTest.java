package com.konggogi.veganlife.comment.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.controller.dto.request.CommentModifyRequest;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.exception.IllegalCommentException;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.comment.service.CommentSearchService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.comment.service.dto.SubCommentDetailsDto;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends RestDocsTest {
    @MockBean CommentService commentService;
    @MockBean CommentSearchService commentSearchService;
    @MockBean CommentLikeService commentLikeService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
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

    @Test
    @DisplayName("댓글 상세 조회 API")
    void getCommentDetailsTest() throws Exception {
        // given
        SubCommentDetailsDto subComment1 =
                new SubCommentDetailsDto(2L, "콩고기M", "좋은 정보 감사합니다!", true, 11, LocalDateTime.now());
        SubCommentDetailsDto subComment2 =
                new SubCommentDetailsDto(3L, "콩고기F", "오오 꿀팁이네요ㅎㅎ", false, 3, LocalDateTime.now());
        CommentDetailsDto detailsDto =
                new CommentDetailsDto(1L, comment, true, 53, List.of(subComment1, subComment2));
        given(commentSearchService.searchDetailsById(anyLong(), anyLong(), anyLong()))
                .willReturn(detailsDto);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(detailsDto.id()))
                .andExpect(
                        jsonPath("$.author").value(detailsDto.comment().getMember().getNickname()))
                .andExpect(jsonPath("$.content").value(detailsDto.comment().getContent()))
                .andExpect(jsonPath("$.isLike").value(detailsDto.isLike()))
                .andExpect(jsonPath("$.likeCount").value(detailsDto.likeCount()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.subComments").isNotEmpty());

        perform.andDo(print())
                .andDo(
                        document(
                                "get-comment-details",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 번호"),
                                        parameterWithName("commentId").description("댓글 번호"))));
    }

    @Test
    @DisplayName("댓글 상세 조회 API - 없는 회원 예외 발생")
    void getCommentDetailsNotFoundMemberTest() throws Exception {
        // given
        given(commentSearchService.searchDetailsById(anyLong(), anyLong(), anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("get-comment-details-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 상세 조회 API - 없는 게시글 예외 발생")
    void getCommentDetailsNotFoundPostTest() throws Exception {
        // given
        given(commentSearchService.searchDetailsById(anyLong(), anyLong(), anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("get-comment-details-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 상세 조회 API - 없는 댓글 예외 발생")
    void getCommentDetailsNotFoundCommentTest() throws Exception {
        // given
        given(commentSearchService.searchDetailsById(anyLong(), anyLong(), anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("get-comment-details-not-found-comment", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 수정 API")
    void modifyTest() throws Exception {
        // given
        CommentModifyRequest request = new CommentModifyRequest("댓글 수정해주세요");
        doNothing()
                .when(commentService)
                .modify(anyLong(), anyLong(), anyLong(), any(CommentModifyRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "modify-comment",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(
                                        parameterWithName("postId").description("게시글 번호"),
                                        parameterWithName("commentId").description("댓글 번호"))));
    }

    @Test
    @DisplayName("댓글 수정 API - 없는 회원 예외 발생")
    void modifyNotFoundMemberTest() throws Exception {
        // given
        CommentModifyRequest request = new CommentModifyRequest("댓글 수정해주세요");
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(commentService)
                .modify(anyLong(), anyLong(), anyLong(), any(CommentModifyRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("modify-comment-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 수정 API - 없는 게시글 예외 발생")
    void modifyNotFoundPostTest() throws Exception {
        // given
        CommentModifyRequest request = new CommentModifyRequest("댓글 수정해주세요");
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(commentService)
                .modify(anyLong(), anyLong(), anyLong(), any(CommentModifyRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("modify-comment-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("댓글 수정 API - 없는 댓글 예외 발생")
    void modifyNotFoundCommentTest() throws Exception {
        // given
        CommentModifyRequest request = new CommentModifyRequest("댓글 수정해주세요");
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT))
                .when(commentService)
                .modify(anyLong(), anyLong(), anyLong(), any(CommentModifyRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("modify-comment-not-found-comment", getDocumentResponse()));
    }

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
