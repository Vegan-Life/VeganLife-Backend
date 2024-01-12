package com.konggogi.veganlife.member.service;


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
    private final MemberMapper memberMapper;

    public void addOrUpdate(Long memberId, String token) {
        memberQueryService
                .searchRefreshToken(memberId)
                .ifPresentOrElse(
                        refreshToken -> {
                            refreshToken.update(token);
                            log.debug("Refresh token updated for memberId: {}", memberId);
                        },
                        () -> {
                            refreshTokenRepository.save(
                                    memberMapper.toRefreshToken(memberId, token));
                            log.debug("New refresh token created for memberId: {}", memberId);
                        });
    }
}
