package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
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
    @InjectMocks PostQueryService postQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);

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
    @DisplayName("게시글 번호로 게시글, 회원 조회")
    void searchWithMemberTest() {
        // given
        given(postRepository.findByIdFetchJoinMember(anyLong())).willReturn(Optional.of(post));
        // when
        Post foundPost = postQueryService.searchWithMember(anyLong());
        // then
        assertThat(foundPost).isEqualTo(post);
        then(postRepository).should().findById(anyLong());
        assertThat(foundPost.getMember()).isNotNull();
    }

    @Test
    @DisplayName("없는 게시글 번호로 게시글 조회시 예외 발생")
    void searchWithMemberNotFoundPostTest() {
        // given
        given(postRepository.findByIdFetchJoinMember(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> postQueryService.searchWithMember(anyLong()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getDescription());
        then(postRepository).should().findById(anyLong());
    }
}
