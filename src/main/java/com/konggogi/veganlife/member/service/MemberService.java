package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.comment.service.CommentLikeService;
import com.konggogi.veganlife.comment.service.CommentService;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.util.AwsS3Folders;
import com.konggogi.veganlife.global.util.AwsS3Uploader;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import com.konggogi.veganlife.member.controller.dto.request.ProfileModifyRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import com.konggogi.veganlife.member.service.dto.MemberLoginDto;
import com.konggogi.veganlife.notification.service.NotificationService;
import com.konggogi.veganlife.post.service.PostLikeService;
import com.konggogi.veganlife.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberMapper memberMapper;
    private final AwsS3Uploader awsS3Uploader;

    public MemberLoginDto login(String email) {
        Member member = addIfNotPresent(email);
        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);
        refreshTokenService.addOrUpdate(member.getId(), refreshToken);
        return memberMapper.toMemberLoginDto(member, accessToken, refreshToken);
    }

    public void removeMember(Long memberId) {
        Member member = memberQueryService.search(memberId);
        removeRelatedData(memberId);
        memberRepository.delete(member);
    }

    public Member modifyProfile(
            Long memberId, ProfileModifyRequest request, MultipartFile profileImage) {
        Member member = memberQueryService.search(memberId);
        validateNickname(member.getNickname(), request.nickname());
        String profileImageUrl = awsS3Uploader.uploadFile(AwsS3Folders.PROFILE, profileImage);
        member.modifyProfile(
                request.nickname(),
                profileImageUrl,
                request.vegetarianType(),
                request.gender(),
                request.birthYear(),
                request.height(),
                request.weight());
        return member;
    }

    private Member addIfNotPresent(String email) {
        return memberRepository
                .findByEmail(email)
                .orElseGet(() -> memberRepository.save(memberMapper.toMember(email)));
    }

    private void validateNickname(String nickname, String newNickname) {
        if (nickname == null || !nickname.equals(newNickname)) {
            memberRepository
                    .findByNickname(newNickname)
                    .ifPresent(
                            member -> {
                                throw new DuplicatedNicknameException(
                                        ErrorCode.DUPLICATED_NICKNAME);
                            });
        }
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
