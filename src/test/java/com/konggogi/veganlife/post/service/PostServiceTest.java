package com.konggogi.veganlife.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;

import com.konggogi.veganlife.global.AwsS3Uploader;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.controller.dto.request.PostFormRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.*;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import com.konggogi.veganlife.post.repository.PostRepository;
import com.konggogi.veganlife.post.repository.TagRepository;
import com.konggogi.veganlife.post.repository.elastic.PostElasticRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock PostRepository postRepository;
    @Spy PostMapper postMapper = new PostMapperImpl();
    @Spy TagMapper tagMapper = new TagMapperImpl();
    @Spy PostImageMapper postImageMapper = new PostImageMapperImpl();
    @Mock TagRepository tagRepository;
    @Mock AwsS3Uploader awsS3Uploader;
    @Mock PostElasticRepository postElasticRepository;
    @InjectMocks PostService postService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Tag tag = TagFixture.DEFAULT.getTag();

    @Test
    @DisplayName("게시글 등록")
    void addTest() {
        // given
        Post post = PostFixture.BAKERY.get();
        Long memberId = member.getId();
        PostFormRequest request = new PostFormRequest("제목", "내용", List.of("태그"));
        given(memberQueryService.search(memberId)).willReturn(member);
        given(postMapper.toEntity(member, request)).willReturn(post);
        given(tagRepository.findByName(anyString())).willReturn(Optional.empty());
        given(tagRepository.save(any(Tag.class))).willReturn(tag);
        given(postRepository.save(post)).willReturn(post);
        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1.png".getBytes()));
        List<String> imageUrls = List.of("image1.png");
        willReturn(imageUrls).given(awsS3Uploader).uploadFiles(eq(AwsS3Folders.COMMUNITY), any());
        // when
        Post savedPost = postService.add(memberId, request, images);
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
        PostFormRequest request = new PostFormRequest("제목", "내용", List.of("태그"));
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(() -> postService.add(memberId, request, List.of()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(memberQueryService).should().search(memberId);
    }

    @Test
    @DisplayName("회원 Id로 Post의 Member null로 변환")
    void removeMemberFromPostTest() {
        // when, then
        assertDoesNotThrow(() -> postService.removeMemberFromPost(member.getId()));
        then(postRepository).should().setMemberToNull(anyLong());
    }

    @Test
    @DisplayName("게시글 수정")
    void modifyTest() {
        // given
        Post post = PostFixture.BAKERY.getWithId(1L, member);
        PostFormRequest request = new PostFormRequest("제목변경", "내용변경", List.of("태그1", "태그2"));
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(tagRepository.save(any(Tag.class))).willReturn(tag);
        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1.png".getBytes()));
        List<String> imageUrls = List.of("image1.png");
        willReturn(imageUrls).given(awsS3Uploader).uploadFiles(eq(AwsS3Folders.COMMUNITY), any());
        // when
        postService.modify(member.getId(), post.getId(), request, images);
        // then
        assertThat(post.getTitle()).isEqualTo(request.title());
        assertThat(post.getContent()).isEqualTo(request.content());
        assertThat(post.getImageUrls()).hasSize(1);
        assertThat(post.getTags()).hasSize(2);
        then(tagRepository).should(atLeastOnce()).save(any(Tag.class));
    }

    @Test
    @DisplayName("게시글 삭제")
    void removeTest() {
        // given
        Post post = PostFixture.BAKERY.getWithId(1L, member);
        given(postQueryService.search(anyLong())).willReturn(post);
        // when
        postService.remove(member.getId());
        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
        then(postRepository).should().delete(any(Post.class));
    }
}
