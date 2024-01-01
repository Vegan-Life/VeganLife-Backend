package com.konggogi.veganlife.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.domain.mapper.LikeMapper;
import com.konggogi.veganlife.like.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Spy LikeMapper likeMapper;
    @Mock LikeQueryService likeQueryService;
    @InjectMocks LikeService likeService;
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final Post post = PostFixture.CHALLENGE.getPostAllInfoWithId(1L);

    @Test
    @DisplayName("게시글 좋아요")
    void addPostLikeTest() {
        // given
        PostLike postLike = PostLike.builder().post(post).member(member).build();
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(likeQueryService.searchPostLike(anyLong(), anyLong())).willReturn(Optional.empty());
        given(likeMapper.toPostLike(any(Member.class))).willReturn(postLike);
        // when
        likeService.addPostLike(member.getId(), post.getId());
        // then
        assertThat(post.getLikes()).contains(postLike);
    }

    @Test
    @DisplayName("게시글 좋아요 없는 회원 예외 발생")
    void addPostLikeNotFoundMemberTest() {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> likeService.addPostLike(member.getId(), post.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should(never()).search(anyLong());
        then(likeQueryService).should(never()).searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 없는 게시글 예외 발생")
    void addPostLikeNotFoundPostTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
        // when, then
        assertThatThrownBy(() -> likeService.addPostLike(member.getId(), post.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(likeQueryService).should(never()).searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("이미 좋아요한 게시글 예외 발생")
    void addPostLikeAlreadyTest() {
        // given
        PostLike postLike = PostLike.builder().post(post).member(member).build();
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(likeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when, then
        assertThatThrownBy(() -> likeService.addPostLike(member.getId(), post.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_POST_LIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(likeQueryService).should().searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 취소")
    void removePostLikeTest() {
        // given
        PostLike postLike = PostLike.builder().post(post).member(member).build();
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(likeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        likeService.removePostLike(member.getId(), post.getId());
        // then
        assertThat(post.getLikes()).doesNotContain(postLike);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 없는 회원 예외 발생")
    void removePostLikeNotFoundMemberTest() {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> likeService.removePostLike(member.getId(), post.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should(never()).search(anyLong());
        then(likeQueryService).should(never()).searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 취소 없는 게시글 예외 발생")
    void removePostLikeNotFoundPostTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
        // when, then
        assertThatThrownBy(() -> likeService.removePostLike(member.getId(), post.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(likeQueryService).should(never()).searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("이미 게시글 좋아요 취소한 경우 예외 발생")
    void removePostLikeAlreadyTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(likeQueryService.searchPostLike(anyLong(), anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> likeService.removePostLike(member.getId(), post.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_POST_UNLIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(likeQueryService).should().searchPostLike(anyLong(), anyLong());
    }
}
