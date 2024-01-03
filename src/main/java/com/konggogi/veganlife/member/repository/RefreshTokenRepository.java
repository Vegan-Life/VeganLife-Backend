package com.konggogi.veganlife.member.repository;


import com.konggogi.veganlife.global.security.jwt.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);
}
