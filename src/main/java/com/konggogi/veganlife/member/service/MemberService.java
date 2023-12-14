package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.exception.DuplicateNicknameException;
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
        Member member = memberQueryService.findMemberById(memberId);
        memberRepository.delete(member);
    }

    public Member modifyMemberInfo(Long memberId, MemberInfoRequest memberInfoRequest) {
        validateNickname(memberInfoRequest.nickname());
        Member member = memberQueryService.findMemberById(memberId);
        member.updateMemberInfo(memberInfoRequest);
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

    private void validateNickname(String nickname) {
        memberRepository
                .findByNickname(nickname)
                .ifPresent(
                        member -> {
                            throw new DuplicateNicknameException(ErrorCode.DUPLICATED_NICKNAME);
                        });
    }
}
