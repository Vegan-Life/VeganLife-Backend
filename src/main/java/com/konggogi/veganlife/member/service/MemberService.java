package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.global.util.JwtUtils;
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
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;

    @Transactional
    public Member addMember(String email) {
        return memberRepository
                .findByEmail(email)
                .orElseGet(
                        () -> {
                            Member member = Member.builder().email(email).build();
                            return memberRepository.save(member);
                        });
    }

    @Transactional
    public void removeMember(Long memberId) {
        Member member = validateMemberExist(memberId);
        memberRepository.delete(member);
    }

    public Member search(Long memberId) {
        return validateMemberExist(memberId);
    }

    @Transactional
    public Member modifyMemberInfo(Long memberId, MemberInfoRequest memberInfoRequest) {
        validateNickname(memberInfoRequest.nickname());
        Member member = validateMemberExist(memberId);
        member.updateMemberInfo(memberInfoRequest);
        return member;
    }

    @Transactional
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

    public String reissueToken(String refreshToken, Long memberId) {
        jwtUtils.validateToken(refreshToken);
        return refreshTokenRepository
                .findRefreshTokenByMemberId(memberId)
                .map(
                        token -> {
                            Member member = validateMemberExist(memberId);
                            if (!token.isSameToken(refreshToken)) {
                                throw new MismatchTokenException(ErrorCode.MISMATCH_REFRESH_TOKEN);
                            }
                            return jwtProvider.createToken(member.getEmail());
                        })
                .orElseThrow(
                        () -> {
                            throw new InvalidJwtException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
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

    private Member validateMemberExist(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
