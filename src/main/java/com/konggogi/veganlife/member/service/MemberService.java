package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.service.PostLikeService;
import com.konggogi.veganlife.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final NotificationService notificationService;
    private final CommentLikeService commentLikeService;
    private final PostLikeService postLikeService;
    private final PostService postService;
    private final CommentService commentService;
    private final MealDataService mealDataService;
    private final MealLogService mealLogService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberMapper memberMapper;

    public Member addIfNotPresent(String email) {
        try {
            return memberQueryService.search(email);
        } catch (NotFoundEntityException e) {
            return memberRepository.save(memberMapper.toMember(email));
        }
    }

    public void removeMember(Long memberId) {
        Member member = memberQueryService.search(memberId);
        removeRelatedData(memberId);
        memberRepository.delete(member);
    }

    public Member modifyMemberInfo(Long memberId, MemberInfoRequest infoRequest) {
        validateNickname(infoRequest.nickname());
        Member member = memberQueryService.search(memberId);
        member.updateMemberInfo(
                infoRequest.nickname(),
                infoRequest.gender(),
                infoRequest.vegetarianType(),
                infoRequest.birthYear(),
                infoRequest.height(),
                infoRequest.weight());
        return member;
    }

    public Member modifyMemberProfile(Long memberId, MemberProfileRequest profileRequest) {
        validateNickname(profileRequest.nickname());
        Member member = memberQueryService.search(memberId);
        member.modifyMemberProfile(
                profileRequest.nickname(),
                profileRequest.imageUrl(),
                profileRequest.vegetarianType(),
                profileRequest.gender(),
                profileRequest.birthYear(),
                profileRequest.height(),
                profileRequest.weight());
        return member;
    }

    private void validateNickname(String nickname) {
        memberRepository
                .findByNickname(nickname)
                .ifPresent(
                        member -> {
                            throw new DuplicatedNicknameException(ErrorCode.DUPLICATED_NICKNAME);
                        });
    }

    private void removeRelatedData(Long memberId) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
        postService.removeMemberFromPost(memberId);
        commentService.removeMemberFromComment(memberId);
        postLikeService.removeMemberFromPostLike(memberId);
        commentLikeService.removeMemberFromCommentLike(memberId);
        notificationService.removeAll(memberId);
        mealDataService.removeAll(memberId);
        mealLogService.removeAll(memberId);
    }
}
