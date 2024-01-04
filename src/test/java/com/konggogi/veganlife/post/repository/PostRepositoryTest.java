package com.konggogi.veganlife.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.post.domain.Post;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PostRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    private final Member member = MemberFixture.DEFAULT_M.get();
    private final Post post =
            Post.builder().member(member).title("title").content("content").build();

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
}
