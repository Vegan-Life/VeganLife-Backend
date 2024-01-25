package com.konggogi.veganlife.comment.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.notification.domain.NotificationMessage;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.service.PostQueryService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public void notifyAddCommentIfNotAuthor(Long authorId, Long postId) {
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

    public void notifyMention(Long postId, Long authorId, String content) {
        Member commentAuthor = memberQueryService.search(authorId);
        Post post = postQueryService.search(postId);
        List<Member> participants = post.getAllParticipants();
        extractMentionedNicknames(content)
                .forEach(
                        nickname ->
                                notifyIfParticipantMentioned(
                                        nickname, participants, commentAuthor, post));
    }

    private void notifyIfParticipantMentioned(
            String nickname, List<Member> participants, Member commentAuthor, Post post) {
        memberQueryService
                .searchByNickname(nickname)
                .ifPresent(
                        mentionedMember -> {
                            if (participants.contains(mentionedMember)
                                    && !mentionedMember.equals(commentAuthor)) {
                                notificationService.sendNotification(
                                        mentionedMember,
                                        NotificationType.MENTION,
                                        NotificationMessage.MENTION.getMessage(
                                                commentAuthor.getNickname(),
                                                post.getMember().getNickname()));
                            }
                        });
    }

    private List<String> extractMentionedNicknames(String content) {
        // 정규 표현식을 사용하여 @로 시작하는 닉네임 추출
        Matcher matcher = Pattern.compile("@[\\p{L}\\p{N}_]+").matcher(content);
        List<String> nicknames = new ArrayList<>();
        while (matcher.find()) {
            nicknames.add(matcher.group().substring(1));
        }
        return nicknames;
    }
}
