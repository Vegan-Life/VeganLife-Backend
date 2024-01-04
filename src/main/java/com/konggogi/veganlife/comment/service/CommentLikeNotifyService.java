package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationMessage;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeNotifyService {
    private final MemberQueryService memberQueryService;
    private final CommentQueryService commentQueryService;
    private final NotificationService notificationService;

    public void notifyAddCommentLikeIfNotAuthor(Long memberId, Long commentId) {
        Member commentLikeMember = memberQueryService.search(memberId);
        Member commentAuthor = commentQueryService.search(commentId).getMember();
        if (commentLikeMember.equals(commentAuthor)) {
            return;
        }
        notificationService.sendNotification(
                commentAuthor,
                NotificationType.COMMENT_LIKE,
                NotificationMessage.COMMENT_LIKE.getMessage(
                        commentLikeMember.getNickname(), commentAuthor.getNickname()));
    }
}
