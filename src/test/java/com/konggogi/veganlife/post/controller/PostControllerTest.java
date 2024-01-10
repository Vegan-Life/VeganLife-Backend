package com.konggogi.veganlife.post.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
import com.konggogi.veganlife.comment.service.dto.SubCommentDetailsDto;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.controller.dto.request.PostFormRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostImageFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import com.konggogi.veganlife.post.service.PostLikeService;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.post.service.PostSearchService;
import com.konggogi.veganlife.post.service.PostService;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import com.konggogi.veganlife.post.service.dto.PostSimpleDto;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PostController.class)
class PostControllerTest extends RestDocsTest {
    @MockBean PostService postService;
    @MockBean PostLikeService postLikeService;
    @MockBean PostSearchService postSearchService;
    @MockBean PostQueryService postQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("게시글 등록 API")
    void addPostTest() throws Exception {
        // given
        Post post = PostFixture.BAKERY.getWithId(1L, member);
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("#맛집");
        PostFormRequest request =
                new PostFormRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        given(postService.add(anyLong(), any(PostFormRequest.class))).willReturn(post);
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
        Post post = PostFixture.BAKERY.getWithId(1L, member);
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("#맛집");
        PostFormRequest request =
                new PostFormRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        given(postService.add(anyLong(), any(PostFormRequest.class)))
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
    @DisplayName("게시글 상세 조회 API")
    void getPostTest() throws Exception {
        // given
        Post post = PostFixture.CHALLENGE.getWithId(1L, member);
        Comment comment1 = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
        Comment subComment = CommentFixture.DEFAULT.getSubCommentWithId(3L, member, post, comment1);
        Comment comment2 = CommentFixture.DEFAULT.getTopCommentWithId(2L, member, post);
        List<String> imageUrls =
                List.of(
                        PostImageFixture.DEFAULT.getImageUrl(),
                        PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("챌린지", "작심삼일");
        SubCommentDetailsDto subComment1 =
                new SubCommentDetailsDto(
                        3L, "콩고기M", "작심이틀로 태그 바꿔주세요~", true, 11, LocalDateTime.now());
        CommentDetailsDto commentDetailsDto1 =
                new CommentDetailsDto(comment1, true, 53, List.of(subComment1));
        CommentDetailsDto commentDetailsDto2 = new CommentDetailsDto(comment2, false, 3, List.of());
        List<CommentDetailsDto> commentDetailsDtos =
                List.of(commentDetailsDto1, commentDetailsDto2);

        PostDetailsDto postDetailsDto =
                new PostDetailsDto(1L, post, true, 34, imageUrls, tags, commentDetailsDtos);
        given(postSearchService.searchDetailsById(anyLong(), anyLong())).willReturn(postDetailsDto);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/posts/{postId}", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.author").value(member.getNickname()))
                .andExpect(
                        jsonPath("$.vegetarianType").value(member.getVegetarianType().toString()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.isLike").value(postDetailsDto.isLike()))
                .andExpect(jsonPath("$.likeCount").value(postDetailsDto.likeCount()))
                .andExpect(jsonPath("$.commentCount").value(postDetailsDto.post().countComments()))
                .andExpect(jsonPath("$.imageUrls").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
                .andExpect(jsonPath("$.comments").isNotEmpty());

        perform.andDo(print())
                .andDo(
                        document(
                                "get-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("게시글 상세 조회 API - 없는 회원 예외 발생")
    void getPostNotFoundMemberTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(postSearchService)
                .searchDetailsById(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/posts/{postId}", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("get-post-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 상세 조회 API - 없는 게시글 예외 발생")
    void getPostNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(postSearchService)
                .searchDetailsById(anyLong(), anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/posts/{postId}", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("get-post-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 전체 조회 API")
    void getAllTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Post post1 = PostFixture.BAKERY.getWithDate(1L, member, LocalDate.of(2023, 5, 25));
        Post post2 = PostFixture.CHALLENGE.getWithDate(2L, member, LocalDate.of(2024, 1, 1));
        List<String> imageUrls =
                List.of(
                        PostImageFixture.DEFAULT.getImageUrl(),
                        PostImageFixture.DEFAULT.getImageUrl());
        List<PostSimpleDto> postSimpleDtos =
                List.of(new PostSimpleDto(post2, imageUrls), new PostSimpleDto(post1, List.of()));
        Page<PostSimpleDto> postSimpleDtoPage =
                PageableExecutionUtils.getPage(postSimpleDtos, pageable, postSimpleDtos::size);
        given(postSearchService.searchAllSimple(any(Pageable.class))).willReturn(postSimpleDtoPage);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts")
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                                .queryParam("sort", "createdAt,DESC"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(postSimpleDtos.size()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));

        perform.andDo(print())
                .andDo(
                        document(
                                "all-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        pageDesc(),
                                        sizeDesc(),
                                        parameterWithName("sort").description("정렬 기준"))));
    }

    @Test
    @DisplayName("게시글 수정 API")
    void modifyPostTest() throws Exception {
        // then
        Post post = PostFixture.BAKERY.get();
        List<String> imageUrls =
                List.of(
                        PostImageFixture.DEFAULT.getImageUrl(),
                        PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("신사동", "맛집", "비건");
        PostFormRequest request =
                new PostFormRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        doNothing().when(postService).modify(anyLong(), anyLong(), any(PostFormRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isCreated());

        perform.andDo(print())
                .andDo(
                        document(
                                "modify-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("게시글 수정 API - 없는 회원 예외 발생")
    void modifyPostNotFoundMemberTest() throws Exception {
        // then
        Post post = PostFixture.BAKERY.get();
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("신사동", "맛집", "비건");
        PostFormRequest request =
                new PostFormRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER))
                .when(postService)
                .modify(anyLong(), anyLong(), any(PostFormRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("modify-post-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 수정 API - 없는 게시글 예외 발생")
    void modifyPostNotFoundPostTest() throws Exception {
        // then
        Post post = PostFixture.BAKERY.get();
        List<String> imageUrls = List.of(PostImageFixture.DEFAULT.getImageUrl());
        List<String> tags = List.of("신사동", "맛집", "비건");
        PostFormRequest request =
                new PostFormRequest(post.getTitle(), post.getContent(), imageUrls, tags);
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(postService)
                .modify(anyLong(), anyLong(), any(PostFormRequest.class));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/posts/{postId}", 1L)
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("modify-post-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 삭제 API")
    void removePostTest() throws Exception {
        // given
        doNothing().when(postService).remove(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNoContent());

        perform.andDo(print())
                .andDo(
                        document(
                                "remove-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                pathParameters(parameterWithName("postId").description("게시글 번호"))));
    }

    @Test
    @DisplayName("게시글 삭제 API - 없는 게시글 예외 발생")
    void removePostNotFoundPostTest() throws Exception {
        // given
        doThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST))
                .when(postService)
                .remove(anyLong());
        // when
        ResultActions perform =
                mockMvc.perform(
                        delete("/api/v1/posts/{postId}", 1L).headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("remove-post-not-found-post", getDocumentResponse()));
    }

    @Test
    @DisplayName("게시글 검색 API")
    void getPostListByKeywordTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Post post1 = PostFixture.BAKERY.getWithDate(1L, member, LocalDate.of(2023, 5, 25));
        Post post2 = PostFixture.BAKERY.getWithDate(2L, member, LocalDate.of(2024, 1, 1));
        List<String> imageUrls =
                List.of(
                        PostImageFixture.DEFAULT.getImageUrl(),
                        PostImageFixture.DEFAULT.getImageUrl());
        List<PostSimpleDto> postSimpleDtos =
                List.of(new PostSimpleDto(post2, imageUrls), new PostSimpleDto(post1, List.of()));
        Page<PostSimpleDto> postSimpleDtoPage =
                PageableExecutionUtils.getPage(postSimpleDtos, pageable, postSimpleDtos::size);
        given(postSearchService.searchSimpleByKeyword(any(Pageable.class), anyString()))
                .willReturn(postSimpleDtoPage);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/posts/search")
                                .headers(authorizationHeader())
                                .queryParam("keyword", "맛집")
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                                .queryParam("sort", "createdAt,DESC"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(postSimpleDtos.size()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));

        perform.andDo(print())
                .andDo(
                        document(
                                "search-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        parameterWithName("keyword").description("검색어"),
                                        pageDesc(),
                                        sizeDesc(),
                                        parameterWithName("sort").description("정렬 기준"))));
    }

    @Test
    @DisplayName("인기 태그 조회 API")
    void getPopularTagsTest() throws Exception {
        // given
        List<Tag> tags = List.of(TagFixture.CHALLENGE.getTag(), TagFixture.STORE.getTag());
        given(postQueryService.searchPopularTags()).willReturn(tags);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/posts/tags").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(
                        document(
                                "popular-tags",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("게시글 좋아요 API")
    void addPostLikeTest() throws Exception {
        // given
        doNothing().when(postLikeService).addPostLike(anyLong(), anyLong());
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
                .when(postLikeService)
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
                .when(postLikeService)
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
                .when(postLikeService)
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
        doNothing().when(postLikeService).removePostLike(anyLong(), anyLong());
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
                .when(postLikeService)
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
                .when(postLikeService)
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
                .when(postLikeService)
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
