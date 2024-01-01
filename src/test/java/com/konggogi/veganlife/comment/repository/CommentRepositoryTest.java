package com.konggogi.veganlife.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
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
class CommentRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final Post post = PostFixture.CHALLENGE.getPost();
    private final Comment comment = CommentFixture.DEFAULT.getTopComment(member, post);

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        postRepository.save(post);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("comment Id로 댓글 조회")
    void findByIdTest() {
        // given
        Long commentId = comment.getId();
        // when
        Optional<Comment> foundComment = commentRepository.findById(commentId);
        // then
        assertThat(foundComment).isPresent();
    }
}
