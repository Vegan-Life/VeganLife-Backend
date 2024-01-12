package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final MemberQueryService memberQueryService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberMapper memberMapper;

    public void addOrUpdate(Long memberId, String token) {
        memberQueryService
                .searchRefreshToken(memberId)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.update(token),
                        () -> {
                            RefreshToken newToken = memberMapper.toRefreshToken(memberId, token);
                            refreshTokenRepository.save(newToken);
                        });
    }
}
