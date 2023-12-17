package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;

    public Member search(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public String reissueToken(String refreshToken) {
        jwtUtils.validateToken(refreshToken);
        Member member = findMemberByToken(refreshToken);
        return refreshTokenRepository
                .findRefreshTokenByMemberId(member.getId())
                .map(
                        token -> {
                            if (!token.isSameToken(refreshToken)) {
                                throw new MismatchTokenException(ErrorCode.MISMATCH_REFRESH_TOKEN);
                            }
                            return jwtProvider.createToken(member.getEmail());
                        })
                .orElseThrow(
                        () -> {
                            throw new NotFoundEntityException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
                        });
    }

    private Member findMemberByToken(String token) {
        return jwtUtils.extractUserEmail(token)
                .flatMap(memberRepository::findByEmail)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
