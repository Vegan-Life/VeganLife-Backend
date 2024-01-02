package com.konggogi.veganlife.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.fixture.PostFixture;
import com.konggogi.veganlife.post.service.PostQueryService;
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
        // when, then
        assertDoesNotThrow(
                () ->
                        commentNotifyService.notifyAddCommentIfNotAuthor(
                                commentAuthor.getId(), post.getId()));
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
}
