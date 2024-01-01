package com.konggogi.veganlife.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.repository.PostLikeRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PostLikeRepositoryTest {
    @Autowired PostLikeRepository postLikeRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    private final Post post = PostFixture.BAKERY.getPost();
    private final Member member = MemberFixture.DEFAULT_F.getMember();
    private PostLike postLike;

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        postRepository.save(post);
        postLike = PostLike.builder().post(post).member(member).build();
        postLikeRepository.save(postLike);
    }

    @Test
    @DisplayName("회원 번호와 게시글 번호로 게시글 찾기")
    void findByMemberIdAndIdTest() {
        // when
        Optional<PostLike> foundPostLike =
                postLikeRepository.findByMemberIdAndId(member.getId(), postLike.getId());
        // then
        assertThat(foundPostLike).isPresent();
    }
}
