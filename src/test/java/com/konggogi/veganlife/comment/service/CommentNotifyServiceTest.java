package com.konggogi.veganlife.comment.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentNotifyServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock PostQueryService postQueryService;
    @Mock NotificationService notificationService;
    @InjectMocks CommentNotifyService commentNotifyService;

    private final Member postAuthor = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Member commentAuthor = MemberFixture.DEFAULT_F.getWithId(2L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L, postAuthor);

    @Test
    @DisplayName("댓글 등록 알림")
    void notifyAddCommentIfNotAuthorTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(commentAuthor);
        given(postQueryService.search(anyLong())).willReturn(post);
        // when
        commentNotifyService.notifyAddCommentIfNotAuthor(commentAuthor.getId(), post.getId());
        // then
        then(notificationService)
                .should()
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("댓글 등록 알림 - 댓글 작성자가 게시글 작성자와 같다면 미알림")
    void notifyAddCommentIfAuthorTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(postAuthor);
        given(postQueryService.search(anyLong())).willReturn(post);
        // when
        commentNotifyService.notifyAddCommentIfNotAuthor(postAuthor.getId(), post.getId());
        // then
        then(notificationService)
                .should(never())
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("댓글 언급 알림")
    void notifyMentionTest() {
        // given
        Comment topComment = CommentFixture.DEFAULT.getTopCommentWithId(1L, commentAuthor, post);
        CommentFixture.DEFAULT.getSubCommentWithId(2L, postAuthor, post, topComment);
        given(memberQueryService.search(anyLong())).willReturn(postAuthor);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(memberQueryService.searchByNickname(anyString()))
                .willReturn(Optional.of(commentAuthor));
        // when
        commentNotifyService.notifyMention(post.getId(), postAuthor.getId(), "@비건라이프F 같이 해요!");
        // then
        then(notificationService)
                .should()
                .sendNotification(eq(commentAuthor), eq(NotificationType.MENTION), anyString());
    }

    @Test
    @DisplayName("댓글 언급 알림 - 작성자가 자신을 언급하면 알림이 가지 않는다")
    void notNotifyMentionIfSelfMentionTest() {
        // given
        Comment topComment = CommentFixture.DEFAULT.getTopCommentWithId(1L, commentAuthor, post);
        CommentFixture.DEFAULT.getSubCommentWithId(2L, postAuthor, post, topComment);
        given(memberQueryService.search(anyLong())).willReturn(postAuthor);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(memberQueryService.searchByNickname(anyString())).willReturn(Optional.of(postAuthor));
        // when
        commentNotifyService.notifyMention(post.getId(), postAuthor.getId(), "@비건라이프M 같이 해요!");
        // then
        then(notificationService)
                .should(never())
                .sendNotification(eq(postAuthor), eq(NotificationType.MENTION), anyString());
    }

    @Test
    @DisplayName("댓글 언급 알림 - 게시글에 존재하지 않는 사용자에게는 알림이 가지 않는다")
    void notifyMentionOnlyParticipantsInPostTest() {
        // given
        Member otherMember = Member.builder().nickname("콩고기").build();
        Comment topComment = CommentFixture.DEFAULT.getTopCommentWithId(1L, commentAuthor, post);
        CommentFixture.DEFAULT.getSubCommentWithId(2L, postAuthor, post, topComment);
        given(memberQueryService.search(anyLong())).willReturn(postAuthor);
        given(postQueryService.search(anyLong())).willReturn(post);
        given(memberQueryService.searchByNickname(commentAuthor.getNickname()))
                .willReturn(Optional.of(commentAuthor));
        given(memberQueryService.searchByNickname(otherMember.getNickname()))
                .willReturn(Optional.of(otherMember));
        // when
        commentNotifyService.notifyMention(post.getId(), postAuthor.getId(), "@비건라이프F @콩고기 같이 해요!");
        // then
        then(notificationService)
                .should()
                .sendNotification(eq(commentAuthor), eq(NotificationType.MENTION), anyString());
        then(notificationService)
                .should(never())
                .sendNotification(eq(otherMember), eq(NotificationType.MENTION), anyString());
    }
}
