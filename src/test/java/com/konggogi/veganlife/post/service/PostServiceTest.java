package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostImageMapper;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.domain.mapper.TagMapper;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostImageFixture;
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

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostRepository postRepository;
    @Mock PostMapper postMapper;
    @Mock TagMapper tagMapper;
    @Mock PostImageMapper postImageMapper;
    @Mock TagRepository tagRepository;
    @InjectMocks PostService postService;
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final Tag tag = TagFixture.DEFAULT.getTag();

    @Test
    @DisplayName("게시글 등록")
    void addTest() {
        // given
        Post post = PostFixture.BAKERY.getPost();
        PostTag postTag = PostTag.builder().tag(tag).build();
        PostImage postImage = PostImageFixture.DEFAULT.getPostImage();
        Long memberId = member.getId();
        PostAddRequest request = createPostAddRequest();
        given(memberQueryService.search(memberId)).willReturn(member);
        given(postMapper.toEntity(member, request)).willReturn(post);
        given(tagRepository.findByName(anyString())).willReturn(Optional.empty());
        given(tagMapper.toEntity(anyString())).willReturn(tag);
        given(postImageMapper.toEntity(anyString())).willReturn(postImage);
        given(tagRepository.save(tag)).willReturn(tag);
        given(tagMapper.toPostTag(tag)).willReturn(postTag);
        given(postRepository.save(post)).willReturn(post);
        // when
        Post savedPost = postService.add(memberId, request);
        // then
        assertThat(savedPost.getImageUrls()).hasSize(1);
        assertThat(savedPost.getTags()).hasSize(1);
        then(memberQueryService).should().search(memberId);
        then(tagRepository).should(atLeastOnce()).findByName(anyString());
        then(tagRepository).should(atLeastOnce()).save(any(Tag.class));
        then(postRepository).should().save(post);
    }

    @Test
    @DisplayName("회원이 아니면 게시글 등록 오류 발생")
    void addNoMemberTest() {
        // given
        Long memberId = member.getId();
        PostAddRequest request = createPostAddRequest();
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> postService.add(memberId, request))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberQueryService).should().search(memberId);
    }

    private PostAddRequest createPostAddRequest() {
        List<String> imageUrls = List.of("image.jpg");
        List<String> tags = List.of("#tag");
        return new PostAddRequest("title", "content", imageUrls, tags);
    }
}
