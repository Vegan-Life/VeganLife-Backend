package com.konggogi.veganlife.member.repository;


import com.konggogi.veganlife.member.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findRefreshTokenByMemberId(Long memberId);
}
