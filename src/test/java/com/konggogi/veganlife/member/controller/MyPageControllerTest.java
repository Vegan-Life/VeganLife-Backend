package com.konggogi.veganlife.member.controller;

import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.konggogi.veganlife.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.domain.Gender;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostImageFixture;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MyPageController.class)
class MyPageControllerTest extends RestDocsTest {
    @MockBean MemberService memberService;
    @MockBean MemberQueryService memberQueryService;
    @MockBean PostSearchService postSearchService;

    @Test
    @DisplayName("회원 프로필 조회 API")
    void getMemberDetailsTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_M.getWithId(1L);
        given(memberQueryService.search(anyLong())).willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/profile").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(
                        jsonPath("$.vegetarianType").value(member.getVegetarianType().toString()))
                .andExpect(jsonPath("$.gender").value(member.getGender().toString()))
                .andExpect(jsonPath("$.imageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("$.birthYear").value(member.getBirthYear()))
                .andExpect(jsonPath("$.height").value(member.getHeight()))
                .andExpect(jsonPath("$.weight").value(member.getWeight()));

        perform.andDo(print())
                .andDo(
                        document(
                                "member-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 프로필 조회 API - 없는 회원 예외 발생")
    void getNotMemberDetailsTest() throws Exception {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(get("/api/v1/members/profile").headers(authorizationHeader()));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("member-profile-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 프로필 수정 API")
    void modifyMemberProfileTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_M.getWithId(1L);
        MemberProfileRequest request =
                new MemberProfileRequest(
                        member.getNickname(),
                        member.getProfileImageUrl(),
                        member.getVegetarianType(),
                        member.getGender(),
                        member.getBirthYear(),
                        member.getHeight(),
                        member.getWeight());
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willReturn(member);
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isOk());

        perform.andDo(print())
                .andDo(
                        document(
                                "modify-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc())));
    }

    @Test
    @DisplayName("회원 프로필 수정 API - 중복된 닉네임 예외 발생")
    void modifyMemberProfileDuplicatedNicknameTest() throws Exception {
        // given
        MemberProfileRequest request =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willThrow(new DuplicatedNicknameException(ErrorCode.DUPLICATED_NICKNAME));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isConflict());

        perform.andDo(print())
                .andDo(document("modify-profile-duplicated-nickname", getDocumentResponse()));
    }

    @Test
    @DisplayName("회원 프로필 수정 API - 없는 회원 예외 발생")
    void modifyNotMemberProfileTest() throws Exception {
        // given
        MemberProfileRequest request =
                new MemberProfileRequest(
                        "nickname", "imageUrl", VegetarianType.LACTO, Gender.M, 1993, 190, 90);
        given(memberService.modifyMemberProfile(anyLong(), any(MemberProfileRequest.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        put("/api/v1/members/profile")
                                .headers(authorizationHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request)));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print()).andDo(document("modify-profile-not-found", getDocumentResponse()));
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 API")
    void getMyPostListTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getWithId(1L);
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
        given(postSearchService.searchAllSimple(anyLong(), any(Pageable.class)))
                .willReturn(postSimpleDtoPage);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/me/posts")
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
                                "get-my-post",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        pageDesc(),
                                        sizeDesc(),
                                        parameterWithName("sort").description("정렬 기준"))));
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 API - 없는 회원 예외 발생")
    void getMyPostListNotFoundMemberTest() throws Exception {
        // given
        given(postSearchService.searchAllSimple(anyLong(), any(Pageable.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/me/posts")
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                                .queryParam("sort", "createdAt,DESC"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(document("get-my-post-not-found-member", getDocumentResponse()));
    }

    @Test
    @DisplayName("내가 작성한 댓글이 있는 게시글 조회 API")
    void getPostListContainingMyCommentTest() throws Exception {
        // given
        Member member = MemberFixture.DEFAULT_F.getWithId(1L);
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
        given(postSearchService.searchByMemberComments(anyLong(), any(Pageable.class)))
                .willReturn(postSimpleDtoPage);
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/me/posts-with-comments")
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
                                "get-post-containing-my-comments",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(authorizationDesc()),
                                queryParameters(
                                        pageDesc(),
                                        sizeDesc(),
                                        parameterWithName("sort").description("정렬 기준"))));
    }

    @Test
    @DisplayName("내가 작성한 댓글이 있는 게시글 조회 API - 없는 회원 예외 발생")
    void getPostListContainingMyCommentNotFoundMemberTest() throws Exception {
        // given
        given(postSearchService.searchByMemberComments(anyLong(), any(Pageable.class)))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/api/v1/members/me/posts-with-comments")
                                .headers(authorizationHeader())
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                                .queryParam("sort", "createdAt,DESC"));
        // then
        perform.andExpect(status().isNotFound());

        perform.andDo(print())
                .andDo(
                        document(
                                "get-post-containing-my-comments-not-found-member",
                                getDocumentResponse()));
    }
}
