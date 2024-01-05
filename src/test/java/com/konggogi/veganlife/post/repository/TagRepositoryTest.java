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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TagRepositoryTest {
    @Autowired TagRepository tagRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired PostTagRepository postTagRepository;

    private final Tag tag = TagFixture.DEFAULT.getTag();

    @BeforeEach
    void setup() {
        tagRepository.save(tag);
    }

    @Test
    @DisplayName("태그명으로 태그 찾기")
    void findByNameTagTest() {
        // when
        String tagName = tag.getName();
        Optional<Tag> foundTag = tagRepository.findByName(tagName);
        // then
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getName()).isEqualTo(tagName);
    }

    @Test
    @DisplayName("인기 태그 찾기 - 작성된 게시글이 5개 이상, 내림차순")
    void searchPopularTagsTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.getWithId(1L);
        List<Tag> otherTags = List.of(TagFixture.CHALLENGE.getTag(), TagFixture.STORE.getTag());
        tagRepository.saveAll(otherTags);
        memberRepository.save(member);
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            posts.add(PostFixture.BAKERY.get(member));
        }
        postRepository.saveAll(posts);
        for (Post post : posts) {
            PostTag postTag1 = PostTagFixture.DEFAULT.get(post, otherTags.get(0));
            PostTag postTag2 = PostTagFixture.DEFAULT.get(post, otherTags.get(1));
            postTagRepository.save(postTag1);
            postTagRepository.save(postTag2);
        }
        PostTag postTag1 = PostTagFixture.DEFAULT.get(posts.get(0), otherTags.get(0));
        postTagRepository.save(postTag1);
        // when
        List<Tag> result = tagRepository.findPopularTags();
        // given
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(otherTags.get(0).getName());
        assertThat(result.get(1).getName()).isEqualTo(otherTags.get(1).getName());
    }

    @Test
    @DisplayName("인기 태그 찾기 - 작성된 게시글이 5개 미만이면 제외")
    void searchPopularTagsNotFoundTest() {
        // given
        Member member = MemberFixture.DEFAULT_M.get();
        memberRepository.save(member);
        Post post = PostFixture.CHALLENGE.get(member);
        postRepository.save(post);
        PostTag postTag = PostTagFixture.DEFAULT.get(post, tag);
        postTagRepository.save(postTag);
        // when
        List<Tag> result = tagRepository.findPopularTags();
        // given
        assertThat(result).isEmpty();
    }
}
