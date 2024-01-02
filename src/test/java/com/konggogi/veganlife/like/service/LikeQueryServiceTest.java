package com.konggogi.veganlife.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.like.domain.CommentLike;
import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.fixture.CommentLikeFixture;
import com.konggogi.veganlife.like.fixture.PostLikeFixture;
import com.konggogi.veganlife.like.repository.CommentLikeRepository;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeQueryServiceTest {
    @Mock PostLikeRepository postLikeRepository;
    @Mock CommentLikeRepository commentLikeRepository;
    @InjectMocks LikeQueryService likeQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final Comment comment = CommentFixture.DEFAULT.getTopComment(member, post);
    private final PostLike postLike = PostLikeFixture.DEFAULT.get(member, post);
    private final CommentLike commentLike = CommentLikeFixture.DEFAULT.get(member, post, comment);

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

    @Test
    @DisplayName("댓글 좋아요 여부 조회 - 존재하는 경우")
    void searchCommentLikeExistTest() {
        // given
        given(commentLikeRepository.findByMemberIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.of(commentLike));
        // when
        Optional<CommentLike> foundCommentLike =
                likeQueryService.searchCommentLike(anyLong(), anyLong());
        // then
        assertThat(foundCommentLike).isPresent();
        then(commentLikeRepository).should().findByMemberIdAndCommentId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 좋아요 여부 조회 - 존재하지 않는 경우")
    void searchCommentLikeNotFoundTest() {
        // given
        given(commentLikeRepository.findByMemberIdAndCommentId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when
        Optional<CommentLike> foundCommentLike =
                likeQueryService.searchCommentLike(anyLong(), anyLong());
        // then
        assertThat(foundCommentLike).isEmpty();
        then(commentLikeRepository).should().findByMemberIdAndCommentId(anyLong(), anyLong());
    }
}
