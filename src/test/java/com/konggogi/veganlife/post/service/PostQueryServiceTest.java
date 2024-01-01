package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.exception.IllegalLikeStatusException;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.repository.PostRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {
    @Mock PostRepository postRepository;
    @Mock PostLikeRepository postLikeRepository;
    @InjectMocks PostQueryService postQueryService;
    private final Post post = PostFixture.CHALLENGE.getPost();
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final PostLike postLike = PostLike.builder().post(post).member(member).build();

    @Test
    @DisplayName("게시글 번호로 게시글 조회")
    void searchTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        // when
        Post foundPost = postQueryService.search(anyLong());
        // then
        assertThat(foundPost).isEqualTo(post);
        then(postRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("없는 게시글 번호로 게시글 조회시 예외 발생")
    void searchNoFoundPostTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> postQueryService.search(anyLong()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getDescription());
        then(postRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 여부 조회 - 존재하는 경우")
    void searchPostLikeExistTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        Optional<PostLike> foundPostLike = postQueryService.searchPostLike(anyLong(), anyLong());
        // then
        assertThat(foundPostLike).isPresent();
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 여부 조회 - 존재하지 않는 경우")
    void searchPostLikeNotFoundTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when
        Optional<PostLike> foundPostLike = postQueryService.searchPostLike(anyLong(), anyLong());
        // then
        assertThat(foundPostLike).isEmpty();
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 존재하는지 검증 - 존재하면 예외 발생")
    void validatePostLikeIsExistTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when, then
        assertThatThrownBy(() -> postQueryService.validatePostLikeIsExist(anyLong(), anyLong()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_LIKED.getDescription());
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 존재하는지 검증 - 존재하지 않으면 예외 미발생")
    void validatePostLikeIsExistNotFoundTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when
        postQueryService.validatePostLikeIsExist(anyLong(), anyLong());
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 존재하지 않는지 검증 - 존재하지 않으면 예외 발생")
    void validatePostLikeIsNotExistTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> postQueryService.validatePostLikeIsNotExist(anyLong(), anyLong()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_UNLIKED.getDescription());
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 존재하지 않는지 검증 - 존재하면 예외 미발생")
    void validatePostLikeIsNotExistFoundTest() {
        // given
        given(postLikeRepository.findByMemberIdAndId(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        postQueryService.validatePostLikeIsNotExist(anyLong(), anyLong());
        then(postLikeRepository).should().findByMemberIdAndId(anyLong(), anyLong());
    }
}
