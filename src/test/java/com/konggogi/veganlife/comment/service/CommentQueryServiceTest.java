package com.konggogi.veganlife.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.repository.CommentRepository;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
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
class CommentQueryServiceTest {
    @Mock CommentRepository commentRepository;
    @InjectMocks CommentQueryService commentQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getPost();
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);

    @Test
    @DisplayName("댓글 조회")
    void searchTest() {
        // given
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        // when
        Comment foundComment = commentQueryService.search(comment.getId());
        // then
        assertThat(foundComment).isEqualTo(comment);
        then(commentRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("없는 댓글 조회시 예외 발생")
    void searchNotFoundCommentTest() {
        // given
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> commentQueryService.search(comment.getId()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_COMMENT.getDescription());
        then(commentRepository).should().findById(anyLong());
    }
}
