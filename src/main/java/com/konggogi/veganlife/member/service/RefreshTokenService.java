package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.controller.dto.response.ReissueTokenResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenService {
    private final MemberQueryService memberQueryService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthMapper authMapper;

    public void addOrUpdate(Long memberId, String bearerRefreshToken) {
        memberQueryService
                .searchRefreshToken(memberId)
                .ifPresentOrElse(
                        storedToken -> update(storedToken, bearerRefreshToken),
                        () -> add(memberId, bearerRefreshToken));
    }

    public ReissueTokenResponse reissueToken(String refreshToken) {
        Member member = memberQueryService.searchByToken(refreshToken);
        RefreshToken storedToken = findMatchingRefreshToken(member.getId(), refreshToken);
        String newRefreshToken = jwtProvider.createRefreshToken(member.getEmail());
        String newAccessToken = jwtProvider.createAccessToken(member.getEmail());
        update(storedToken, newRefreshToken);
        return authMapper.toReissueTokenResponse(newAccessToken, newRefreshToken);
    }

    private void add(Long memberId, String bearerRefreshToken) {
        refreshTokenRepository.save(authMapper.toRefreshToken(memberId, bearerRefreshToken));
        log.debug("New refresh token created for memberId: {}", memberId);
    }

    private void update(RefreshToken refreshToken, String bearerRefreshToken) {
        refreshToken.update(bearerRefreshToken);
        log.debug("Refresh token updated for memberId: {}", refreshToken.getMemberId());
    }

    private RefreshToken findMatchingRefreshToken(Long memberId, String refreshToken) {
        return memberQueryService
                .searchRefreshToken(memberId)
                .filter(storedToken -> storedToken.isSameToken(refreshToken))
                .orElseThrow(
                        () -> new MismatchTokenException(ErrorCode.NOT_FOUND_MATCH_REFRESH_TOKEN));
    }
}
