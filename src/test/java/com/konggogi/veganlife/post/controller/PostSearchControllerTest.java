package com.konggogi.veganlife.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostImageFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.post.service.PostSearchService;
import com.konggogi.veganlife.post.service.dto.PostSimpleDto;
import com.konggogi.veganlife.support.docs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PostSearchController.class)
class PostSearchControllerTest extends RestDocsTest {
    @MockBean PostQueryService postQueryService;
    @MockBean PostSearchService postSearchService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

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
}
