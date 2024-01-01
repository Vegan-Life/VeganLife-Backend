package com.konggogi.veganlife.like.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.like.domain.PostLike;
import com.konggogi.veganlife.like.fixture.PostLikeFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.repository.PostRepository;
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
    private final PostLike postLike = PostLikeFixture.DEFAULT.get(member, post);

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        postRepository.save(post);
        postLikeRepository.save(postLike);
    }

    @Test
    @DisplayName("회원 번호와 게시글 번호로 좋아요 찾기")
    void findByMemberIdAndPostIdTest() {
        // when
        Optional<PostLike> foundPostLike =
                postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId());
        // then
        assertThat(foundPostLike).isPresent();
    }
}
