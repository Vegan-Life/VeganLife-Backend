package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.service.CommentSearchService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.domain.mapper.PostMapperImpl;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostLikeFixture;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostSearchServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock PostLikeQueryService postLikeQueryService;
    @Mock CommentSearchService commentSearchService;
    @Spy PostMapper postMapper = new PostMapperImpl();
    @InjectMocks PostSearchService postSearchService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, member);
    private final Comment comment = CommentFixture.DEFAULT.getTopCommentWithId(1L, member, post);
    private final PostLike postLike = PostLikeFixture.DEFAULT.get(member, post);

    @Test
    @DisplayName("게시글 상세 조회")
    void searchDetailsByIdTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.searchWithMember(anyLong())).willReturn(post);
        given(postLikeQueryService.searchPostLike(anyLong(), anyLong()))
                .willReturn(Optional.of(postLike));
        // when
        PostDetailsDto result = postSearchService.searchDetailsById(member.getId(), post.getId());
        // then
        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.post()).isEqualTo(post);
        assertThat(result.isLike()).isTrue();
        assertThat(result.likeCount()).isEqualTo(1);
        assertThat(result.imageUrls()).hasSize(1);
        assertThat(result.tags()).hasSize(1);
        assertThat(result.comments()).hasSize(1);
    }
}
