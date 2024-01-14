package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.global.security.exception.MismatchTokenException;
import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
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
    private final MemberMapper memberMapper;

    public void addOrUpdate(Long memberId, String bearerRefreshToken) {
        memberQueryService
                .searchRefreshToken(memberId)
                .ifPresentOrElse(
                        storedToken -> {
                            storedToken.update(bearerRefreshToken);
                            log.debug("Refresh token updated for memberId: {}", memberId);
                        },
                        () -> {
                            add(memberId, bearerRefreshToken);
                            log.debug("New refresh token created for memberId: {}", memberId);
                        });
    }

    public String reissueAccessToken(String refreshToken) {
        Member member = memberQueryService.searchByToken(refreshToken);
        return memberQueryService
                .searchRefreshToken(member.getId())
                .map(
                        storedToken -> {
                            validateIsSameToken(storedToken, refreshToken);
                            return jwtProvider.createToken(member.getEmail());
                        })
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));
    }

    private void add(Long memberId, String refreshToken) {
        refreshTokenRepository.save(memberMapper.toRefreshToken(memberId, refreshToken));
    }

    private void validateIsSameToken(RefreshToken refreshToken, String token) {
        if (!refreshToken.isSameToken(token)) {
            throw new MismatchTokenException(ErrorCode.MISMATCH_REFRESH_TOKEN);
        }
    }
}
