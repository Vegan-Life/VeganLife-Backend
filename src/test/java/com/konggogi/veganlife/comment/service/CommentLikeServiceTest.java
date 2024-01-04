package com.konggogi.veganlife.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.CommentLike;
import com.konggogi.veganlife.comment.domain.mapper.CommentLikeMapper;
import com.konggogi.veganlife.comment.domain.mapper.CommentLikeMapperImpl;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.fixture.CommentLikeFixture;
import com.konggogi.veganlife.comment.repository.CommentLikeRepository;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.exception.IllegalLikeStatusException;
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
class CommentLikeServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock CommentQueryService commentQueryService;
    @Mock CommentLikeQueryService commentLikeQueryService;
    @Mock CommentLikeNotifyService commentLikeNotifyService;
    @Mock CommentLikeRepository commentLikeRepository;
    @Spy CommentLikeMapper commentLikeMapper = new CommentLikeMapperImpl();
    @InjectMocks CommentLikeService commentLikeService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
    private final CommentLike commentLike = CommentLikeFixture.DEFAULT.get(member, post, comment);

    @Test
    @DisplayName("댓글 좋아요")
    void addCommentLikeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        given(commentLikeQueryService.searchCommentLike(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        //        given(likeMapper.toCommentLike(any(Member.class),
        // any(Post.class))).willReturn(commentLike);
        doNothing()
                .when(commentLikeNotifyService)
                .notifyAddCommentLikeIfNotAuthor(anyLong(), anyLong());
        // when
        commentLikeService.addCommentLike(member.getId(), post.getId(), comment.getId());
        // then
        assertThat(comment.getLikes()).contains(commentLike);
    }

    @Test
    @DisplayName("댓글 좋아요 없는 댓글 예외 발생")
    void addCommentLikeNotFoundCommentTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT));
        // when, then
        assertThatThrownBy(
                        () ->
                                commentLikeService.addCommentLike(
                                        member.getId(), post.getId(), comment.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_COMMENT.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(commentQueryService).should().search(anyLong());
        then(commentLikeQueryService).should(never()).searchCommentLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 좋아요 이미 좋아요한 경우 예외 발생")
    void addCommentLikeAlreadyLikeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        given(commentLikeQueryService.searchCommentLike(anyLong(), anyLong()))
                .willReturn(Optional.of(commentLike));
        // when, then
        assertThatThrownBy(
                        () ->
                                commentLikeService.addCommentLike(
                                        member.getId(), post.getId(), comment.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_COMMENT_LIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(commentQueryService).should().search(anyLong());
        then(commentLikeQueryService).should().searchCommentLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("댓글 좋아요 취소")
    void removeCommentLikeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        given(commentLikeQueryService.searchCommentLike(anyLong(), anyLong()))
                .willReturn(Optional.of(commentLike));
        // when
        commentLikeService.removeCommentLike(member.getId(), post.getId(), comment.getId());
        // then
        assertThat(comment.getLikes()).doesNotContain(commentLike);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 이미 좋아요 취소한 경우 예외 발생")
    void removeCommentLikeNotFoundMemberTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        given(commentLikeQueryService.searchCommentLike(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(
                        () ->
                                commentLikeService.removeCommentLike(
                                        member.getId(), post.getId(), comment.getId()))
                .isInstanceOf(IllegalLikeStatusException.class)
                .hasMessageContaining(ErrorCode.ALREADY_COMMENT_UNLIKED.getDescription());
        then(memberQueryService).should().search(anyLong());
        then(postQueryService).should().search(anyLong());
        then(commentQueryService).should().search(anyLong());
        then(commentLikeQueryService).should().searchCommentLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("회원 Id로 댓글 좋아요의 Member null로 변환")
    void removeMemberFromCommentTest() {
        // when, then
        assertDoesNotThrow(() -> commentLikeService.removeMemberFromCommentLike(member.getId()));
        then(commentLikeRepository).should().setMemberToNull(anyLong());
    }
}
