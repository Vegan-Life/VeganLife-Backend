package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.controller.dto.request.SignupRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.RefreshToken;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import com.konggogi.veganlife.member.service.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberMapper memberMapper;

    public Member addMember(UserInfo userInfo, SignupRequest request) {
        validateNickname(request.nickname());
        return memberRepository.save(memberMapper.toEntity(userInfo, request));
    }

    public void removeMember(Long memberId) {
        Member member = memberQueryService.search(memberId);
        memberRepository.delete(member);
    }

    public void saveRefreshToken(Long memberId, String token) {
        refreshTokenRepository
                .findRefreshTokenByMemberId(memberId)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(token),
                        () -> {
                            RefreshToken newRefreshToken = new RefreshToken(token, memberId);
                            refreshTokenRepository.save(newRefreshToken);
                        });
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
}
