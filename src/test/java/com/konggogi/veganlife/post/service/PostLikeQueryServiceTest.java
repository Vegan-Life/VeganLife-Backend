package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostLikeFixture;
import com.konggogi.veganlife.post.repository.PostLikeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostLikeQueryServiceTest {
    @Mock PostLikeRepository postLikeRepository;
    @InjectMocks PostLikeQueryService likeQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final PostLike postLike = PostLikeFixture.DEFAULT.getWithId(1L, member, post);

    @Test
    @DisplayName("게시글 좋아요 여부 조회 - 존재하는 경우")
    void searchPostLikeExistTest() {
        // given
        given(postLikeRepository.findByMemberIdAndPostId(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        Optional<PostLike> foundPostLike = likeQueryService.searchPostLike(anyLong(), anyLong());
        // then
        assertThat(foundPostLike).isPresent();
        then(postLikeRepository).should().findByMemberIdAndPostId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 좋아요 여부 조회 - 존재하지 않는 경우")
    void searchPostLikeNotFoundTest() {
        // given
        given(postLikeRepository.findByMemberIdAndPostId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when
        Optional<PostLike> foundPostLike = likeQueryService.searchPostLike(anyLong(), anyLong());
        // then
        assertThat(foundPostLike).isEmpty();
        then(postLikeRepository).should().findByMemberIdAndPostId(anyLong(), anyLong());
    }
}
