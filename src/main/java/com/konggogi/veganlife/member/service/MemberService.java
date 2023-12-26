package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.exception.DuplicatedNicknameException;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
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

    public Member addMember(String email) {
        return memberRepository
                .findByEmail(email)
                .orElseGet(
                        () -> {
                            Member member = Member.builder().email(email).build();
                            return memberRepository.save(member);
                        });
    }

    public void removeMember(Long memberId) {
        Member member = memberQueryService.search(memberId);
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
