package com.konggogi.veganlife.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.domain.CommentLike;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.fixture.CommentLikeFixture;
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
class CommentLikeRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentLikeRepository commentLikeRepository;

    private final Post post = PostFixture.BAKERY.get();
    private final Member member = MemberFixture.DEFAULT_F.get();
    private final Comment comment = CommentFixture.DEFAULT.getTopComment(member, post);
    private final CommentLike commentLike = CommentLikeFixture.DEFAULT.get(member, post, comment);

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        postRepository.save(post);
        commentRepository.save(comment);
        commentLikeRepository.save(commentLike);
    }

    @Test
    @DisplayName("회원 번호와 댓글 번호로 좋아요 찾기")
    void findByMemberIdAndCommentIdTest() {
        // when
        Optional<CommentLike> foundCommentLike =
                commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId());
        // then
        assertThat(foundCommentLike).isPresent();
    }

    @Test
    @DisplayName("회원의 댓글 좋아요 Member를 null로 변경")
    void setMemberToNullTest() {
        // given
        Member otherMember = MemberFixture.DEFAULT_M.get();
        memberRepository.save(otherMember);
        Post otherPost = PostFixture.CHALLENGE.get();
        postRepository.save(otherPost);
        Comment otherComment = CommentFixture.DEFAULT.getTopComment(otherMember, otherPost);
        commentRepository.save(otherComment);
        CommentLike otherCommentLike =
                CommentLikeFixture.DEFAULT.get(otherMember, otherPost, otherComment);
        commentLikeRepository.save(otherCommentLike);
        // when
        commentLikeRepository.setMemberToNull(member.getId());
        // then
        assertThat(commentLikeRepository.findById(commentLike.getId()).get().getMember()).isNull();
        assertThat(commentLikeRepository.findById(otherCommentLike.getId()).get().getMember())
                .isNotNull();
    }
}
