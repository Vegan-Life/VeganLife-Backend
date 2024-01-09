package com.konggogi.veganlife.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostTagFixture;
import com.konggogi.veganlife.post.fixture.TagFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class PostRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired PostTagRepository postTagRepository;
    @Autowired TagRepository tagRepository;
    private final Member member = MemberFixture.DEFAULT_M.get();
    private final Post post = PostFixture.BAKERY.get(member);

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        postRepository.save(post);
    }

    @Test
    @DisplayName("회원이 작성한 게시글의 Member를 null로 변경")
    void setMemberToNullTest() {
        // given
        Member otherMember = MemberFixture.DEFAULT_F.get();
        memberRepository.save(otherMember);
        Post otherPost =
                Post.builder().member(otherMember).title("title").content("content").build();
        postRepository.save(otherPost);
        // when
        postRepository.setMemberToNull(member.getId());
        // then
        assertThat(postRepository.findById(post.getId()).get().getMember()).isNull();
        assertThat(postRepository.findById(otherPost.getId()).get().getMember()).isNotNull();
    }

    @Test
    @DisplayName("Post Id로 게시글 및 회원 조회")
    void findByIdFetchJoinMemberTest() {
        // when
        Optional<Post> foundPost = postRepository.findByIdFetchJoinMember(post.getId());
        // then
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getMember()).isNotNull();
    }

    @Test
    @DisplayName("전체 게시글 조회")
    void findAllTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        // when
        Page<Post> posts = postRepository.findAll(pageable);
        // then
        assertThat(posts).hasSize(1);
    }

    @Test
    @DisplayName("검색어로 게시글 조회 - 제목에 포함")
    void findByKeywordContentContainingTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "맛집";
        // when
        Page<Post> posts = postRepository.findByKeyword(keyword, pageable);
        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색어로 게시글 조회 - 내용에 포함")
    void findByKeywordTitleContainingTest() {
        // given
        Post otherPost = Post.builder().title("맛집").content("안녕하세요").build();
        postRepository.save(otherPost);
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "안녕";
        // when
        Page<Post> posts = postRepository.findByKeyword(keyword, pageable);
        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색어로 게시글 조회 - 태그에 포함")
    void findByKeywordTagContainingTest() {
        // given
        Tag tag = TagFixture.DEFAULT.getTag();
        tagRepository.save(tag);
        Post otherPost = Post.builder().title("맛집").content("안녕하세요").build();
        postRepository.save(otherPost);
        PostTag postTag = PostTagFixture.DEFAULT.get(otherPost, tag);
        postTagRepository.save(postTag);
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "태그";
        // when
        Page<Post> posts = postRepository.findByKeyword(keyword, pageable);
        // then
        assertThat(posts).hasSize(1);
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }
}
