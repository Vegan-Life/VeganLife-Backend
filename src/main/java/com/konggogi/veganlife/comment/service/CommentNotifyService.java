package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationMessage;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.service.PostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentNotifyService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final NotificationService notificationService;

    public void notifyAddCommentIfNotPostOwner(Long authorId, Long postId) {
        Member commentAuthor = memberQueryService.search(authorId);
        Member postAuthor = postQueryService.search(postId).getMember();
        if (commentAuthor.equals(postAuthor)) {
            return;
        }
        notificationService.sendNotification(
                postAuthor,
                NotificationType.COMMENT,
                NotificationMessage.ADD_COMMENT.getMessage(
                        commentAuthor.getNickname(), postAuthor.getNickname()));
    }
}
