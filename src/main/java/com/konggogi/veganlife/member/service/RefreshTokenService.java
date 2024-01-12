package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void add(Long memberId, String token) {
        refreshTokenRepository
                .findRefreshTokenByMemberId(memberId)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(token),
                        () -> {
                            RefreshToken newRefreshToken = new RefreshToken(token, memberId);
                            refreshTokenRepository.save(newRefreshToken);
                        });
    }
}
