package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;
import com.konggogi.veganlife.post.domain.mapper.PostLikeMapper;
import com.konggogi.veganlife.post.domain.mapper.PostLikeMapperImpl;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostLikeFixture;
import com.konggogi.veganlife.post.repository.PostLikeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock PostLikeQueryService postLikeQueryService;
    @Mock PostLikeRepository postLikeRepository;
    @Spy PostLikeMapper postLikeMapper = new PostLikeMapperImpl();
    @InjectMocks PostLikeService postLikeService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final PostLike postLike = PostLikeFixture.DEFAULT.get(member, post);

    @Test
    @DisplayName("게시글 좋아요")
    void addPostLikeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(postLikeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        given(postLikeMapper.toPostLike(any(Member.class))).willReturn(postLike);
        // when
        postLikeService.addPostLike(member.getId(), post.getId());
        // then
        assertThat(post.getLikes()).contains(postLike);
    }

    @Test
    @DisplayName("이미 좋아요한 게시글 예외 발생")
    void addPostLikeAlreadyTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(postLikeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when, then
        assertThatThrownBy(() -> postLikeService.addPostLike(member.getId(), post.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_POST_LIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(postLikeQueryService).should().searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 취소")
    void removePostLikeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(postLikeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        postLikeService.removePostLike(member.getId(), post.getId());
        // then
        assertThat(post.getLikes()).doesNotContain(postLike);
    }

    @Test
    @DisplayName("이미 게시글 좋아요 취소한 경우 예외 발생")
    void removePostLikeAlreadyTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(postLikeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> postLikeService.removePostLike(member.getId(), post.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_POST_UNLIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(postLikeQueryService).should().searchPostLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("회원 Id로 게시글 좋아요의 Member null로 변환")
    void removeMemberFromCommentTest() {
        // when, then
        assertDoesNotThrow(() -> postLikeService.removeMemberFromPostLike(member.getId()));
        then(postLikeRepository).should().setMemberToNull(anyLong());
    }
}
