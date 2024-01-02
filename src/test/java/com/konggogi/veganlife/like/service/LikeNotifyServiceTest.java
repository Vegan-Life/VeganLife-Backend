package com.konggogi.veganlife.like.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.comment.domain.Comment;
import com.konggogi.veganlife.comment.fixture.CommentFixture;
import com.konggogi.veganlife.comment.service.CommentQueryService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeNotifyServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock CommentQueryService commentQueryService;
    @Mock NotificationService notificationService;
    @InjectMocks LikeNotifyService likeNotifyService;
    private final Member commentAuthor = MemberFixture.DEFAULT_M.getWithId(1L);
    private final Member commentLikeMember = MemberFixture.DEFAULT_F.getWithId(2L);
    private final Post post = PostFixture.CHALLENGE.getWithId(1L);
    private final Comment comment =
            CommentFixture.DEFAULT.getTopCommentWithId(1L, commentAuthor, post);

    @Test
    @DisplayName("댓글 좋아요 알림")
    void notifyAddCommentLikeIfNotAuthorTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(commentLikeMember);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        // when, then
        assertDoesNotThrow(
                () ->
                        likeNotifyService.notifyAddCommentLikeIfNotAuthor(
                                commentLikeMember.getId(), comment.getId()));
        then(notificationService)
                .should()
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("댓글 좋아요 알림 - 좋아요한 회원이 댓글 작성자와 같다면 미알림")
    void notNotifyAddCommentLikeIfAuthorTest() {
        // given
        given(memberQueryService.search(anyLong())).willReturn(commentAuthor);
        given(commentQueryService.search(anyLong())).willReturn(comment);
        // when
        likeNotifyService.notifyAddCommentLikeIfNotAuthor(commentAuthor.getId(), comment.getId());
        // then
        then(notificationService)
                .should(never())
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }
}
