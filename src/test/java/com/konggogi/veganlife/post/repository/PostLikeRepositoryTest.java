package com.konggogi.veganlife.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostLike;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.fixture.PostLikeFixture;
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

    private final Post post = PostFixture.BAKERY.get();
    private final Member member = MemberFixture.DEFAULT_F.get();
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

    @Test
    @DisplayName("회원의 게시글 좋아요 Member를 null로 변경")
    void setMemberToNullTest() {
        // given
        Member otherMember = MemberFixture.DEFAULT_M.get();
        memberRepository.save(otherMember);
        Post otherPost = PostFixture.CHALLENGE.get();
        postRepository.save(otherPost);
        PostLike otherPostLike = PostLikeFixture.DEFAULT.get(otherMember, otherPost);
        postLikeRepository.save(otherPostLike);
        // when
        postLikeRepository.setMemberToNull(member.getId());
        // then
        assertThat(postLikeRepository.findById(postLike.getId()).get().getMember()).isNull();
        assertThat(postLikeRepository.findById(otherPostLike.getId()).get().getMember())
                .isNotNull();
    }
}
