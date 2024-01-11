package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import com.konggogi.veganlife.post.repository.PostRepository;
import com.konggogi.veganlife.post.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {
    @Mock PostRepository postRepository;
    @Mock TagRepository tagRepository;
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
        then(postRepository).should().findByIdFetchJoinMember(anyLong());
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
        then(postRepository).should().findByIdFetchJoinMember(anyLong());
    }

    @Test
    @DisplayName("전체 게시글 조회")
    void searchAllTest() {
        // given
        Post otherPost = PostFixture.BAKERY.getWithId(2L, member);
        List<Post> posts = List.of(post, otherPost);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> foundPosts = PageableExecutionUtils.getPage(posts, pageable, posts::size);
        given(postRepository.findAll(any(Pageable.class))).willReturn(foundPosts);
        // when
        Page<Post> result = postQueryService.searchAll(pageable);
        // then
        assertThat(result).hasSize(posts.size());
        then(postRepository).should().findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("인기 태그 조회")
    void searchPopularTagsTest() {
        // given
        List<Tag> tags = List.of(TagFixture.CHALLENGE.getTag(), TagFixture.STORE.getTag());
        given(tagRepository.findPopularTags()).willReturn(tags);
        // when
        List<Tag> foundTags = postQueryService.searchPopularTags();
        // then
        assertThat(foundTags).isEqualTo(tags);
    }

    @Test
    @DisplayName("검색어로 게시글 조회")
    void searchByKeywordTest() {
        // given
        Post otherPost = PostFixture.BAKERY.getWithId(2L, member);
        List<Post> posts = List.of(post, otherPost);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> foundPosts = PageableExecutionUtils.getPage(posts, pageable, posts::size);
        given(postRepository.findByKeyword(anyString(), any(Pageable.class)))
                .willReturn(foundPosts);
        // when
        Page<Post> result = postQueryService.searchByKeyword(pageable, "맛집");
        // then
        assertThat(result).hasSize(posts.size());
        then(postRepository).should().findByKeyword(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("회원 Id로 모든 게시글 조회")
    void searchAllByMemberIdTest() {
        // given
        List<Post> posts = List.of(post);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> foundPosts = PageableExecutionUtils.getPage(posts, pageable, posts::size);
        given(postRepository.findAllByMemberId(anyLong(), any(Pageable.class)))
                .willReturn(foundPosts);
        // when
        Page<Post> result = postQueryService.searchAll(member.getId(), pageable);
        // then
        assertThat(result).hasSize(posts.size());
        then(postRepository).should().findAllByMemberId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("회원 Id로 작성된 댓글이 있는 게시글 모두 조회")
    void searchByMemberCommentsTest() {
        // given
        List<Post> posts = List.of(post);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> foundPosts = PageableExecutionUtils.getPage(posts, pageable, posts::size);
        given(postRepository.findByMemberComments(anyLong(), any(Pageable.class)))
                .willReturn(foundPosts);
        // when
        Page<Post> result = postQueryService.searchByMemberComments(member.getId(), pageable);
        // then
        assertThat(result).hasSize(posts.size());
        then(postRepository).should().findByMemberComments(anyLong(), any(Pageable.class));
    }
}
