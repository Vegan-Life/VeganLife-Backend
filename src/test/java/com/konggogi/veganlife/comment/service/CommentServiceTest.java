package com.konggogi.veganlife.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.comment.controller.dto.request.CommentAddRequest;
import com.konggogi.veganlife.comment.controller.dto.request.CommentModifyRequest;
import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapperImpl;
import com.konggogi.veganlife.comment.exception.IllegalCommentException;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.repository.CommentRepository;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock CommentQueryService commentQueryService;
    @Mock CommentNotifyService commentNotifyService;
    @Mock CommentRepository commentRepository;
    @Spy CommentMapper commentMapper = new CommentMapperImpl();
    @InjectMocks CommentService commentService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
    private final Comment subComment =
            CommentFixture.DEFAULT.getSubCommentWithId(2L, member, post, comment);

    @Test
    @DisplayName("최상위 댓글 등록")
    void addTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(null, comment.getContent());
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(commentMapper.toEntity(member, commentAddRequest)).willReturn(comment);
        given(postQueryService.search(anyLong())).willReturn(post);
        doNothing().when(commentNotifyService).notifyAddCommentIfNotAuthor(anyLong(), anyLong());
        doNothing().when(commentNotifyService).notifyMention(anyLong(), anyLong(), anyString());
        // when
        Comment savedComment = commentService.add(member.getId(), post.getId(), commentAddRequest);
        // then
        assertThat(savedComment).isEqualTo(comment);
        assertThat(post.getComments()).contains(comment);
        then(commentQueryService).should(never()).search(anyLong());
    }

    @Test
    @DisplayName("대댓글 등록")
    void addSubTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(1L, comment.getContent());
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(commentMapper.toEntity(member, commentAddRequest)).willReturn(subComment);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        given(postQueryService.search(anyLong())).willReturn(post);
        doNothing().when(commentNotifyService).notifyAddCommentIfNotAuthor(anyLong(), anyLong());
        // when
        Comment savedComment = commentService.add(member.getId(), post.getId(), commentAddRequest);
        // then
        assertThat(savedComment).isEqualTo(subComment);
        assertThat(post.getComments()).contains(subComment);
        assertThat(comment.getSubComments()).contains(subComment);
        then(commentQueryService).should().search(anyLong());
    }

    @Test
    @DisplayName("댓글 등록 시 없는 회원이면 예외 발생")
    void addNotFoundMemberTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(1L, comment.getContent());
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () -> commentService.add(member.getId(), post.getId(), commentAddRequest))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(commentQueryService).should(never()).search(anyLong());
        then(postQueryService).should(never()).search(anyLong());
    }

    @Test
    @DisplayName("댓글 등록 시 없는 게시글이면 예외 발생")
    void addNotFoundPostTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(null, comment.getContent());
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
        // when, then
        assertThatThrownBy(
                        () -> commentService.add(member.getId(), post.getId(), commentAddRequest))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getDescription());
        then(commentQueryService).should(never()).search(anyLong());
        then(postQueryService).should().search(anyLong());
    }

    @Test
    @DisplayName("대댓글 등록 시 최상위 댓글이 없으면 예외 발생")
    void addIllegalCommentTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(1L, comment.getContent());
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(commentQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_COMMENT));
        // when, then
        assertThatThrownBy(
                        () -> commentService.add(member.getId(), post.getId(), commentAddRequest))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_COMMENT.getDescription());
        then(postQueryService).should(never()).search(anyLong());
    }

    @Test
    @DisplayName("대댓글에 댓글을 등록하면 예외 발생")
    void addNotFoundParentCommentTest() {
        // given
        CommentAddRequest commentAddRequest = new CommentAddRequest(2L, comment.getContent());
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(commentQueryService.search(anyLong())).willReturn(subComment);
        // when, then
        assertThatThrownBy(
                        () -> commentService.add(member.getId(), post.getId(), commentAddRequest))
                .isInstanceOf(IllegalCommentException.class)
                .hasMessageContaining(ErrorCode.IS_NOT_PARENT_COMMENT.getDescription());
        then(commentQueryService).should().search(anyLong());
        then(postQueryService).should(never()).search(anyLong());
    }

    @Test
    @DisplayName("회원 Id로 댓글의 Member null로 변환")
    void removeMemberFromCommentTest() {
        // when, then
        assertDoesNotThrow(() -> commentService.removeMemberFromComment(member.getId()));
        then(commentRepository).should().setMemberToNull(anyLong());
    }

    @Test
    @DisplayName("댓글 수정")
    void modifyTest() {
        // given
        CommentModifyRequest commentModifyRequest = new CommentModifyRequest("수정된 댓글");
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        // when
        commentService.modify(member.getId(), post.getId(), comment.getId(), commentModifyRequest);
        // then
        assertThat(comment.getContent()).isEqualTo(commentModifyRequest.content());
    }

    @Test
    @DisplayName("댓글 삭제")
    void removeTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        // when
        commentService.remove(member.getId(), post.getId(), comment.getId());
        // then
        then(commentRepository).should().delete(any(Comment.class));
    }
}
