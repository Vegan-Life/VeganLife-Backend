package com.konggogi.veganlife.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.CommentLike;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapper;
import com.konggogi.veganlife.comment.domain.mapper.CommentMapperImpl;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.fixture.CommentLikeFixture;
import com.konggogi.veganlife.comment.service.dto.CommentDetailsDto;
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
class CommentSearchServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock CommentQueryService commentQueryService;
    @Mock CommentLikeQueryService commentLikeQueryService;
    @Spy CommentMapper commentMapper = new CommentMapperImpl();
    @InjectMocks CommentSearchService commentSearchService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
    private final Comment subComment =
            CommentFixture.DEFAULT.getSubCommentWithId(2L, member, post, comment);
    private final CommentLike commentLike = CommentLikeFixture.DEFAULT.get(member, post, comment);

    @Test
    @DisplayName("댓글 상세 조회")
    void searchDetailsByIdTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(commentQueryService.searchWithMember(anyLong())).willReturn(comment);
        given(commentLikeQueryService.searchCommentLike(anyLong(), anyLong()))
                .willReturn(Optional.of(commentLike));
        // when
        CommentDetailsDto result =
                commentSearchService.searchDetailsById(
                        member.getId(), post.getId(), comment.getId());
        // then
        assertThat(result.id()).isEqualTo(comment.getId());
        assertThat(result.comment()).isEqualTo(comment);
        assertThat(result.isLike()).isTrue();
        assertThat(result.likeCount()).isEqualTo(1);
        assertThat(result.subComments()).hasSize(1);
        assertThat(result.subComments().get(0).id()).isEqualTo(subComment.getId());
    }
}
