package com.konggogi.veganlife.global.security.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

    public JwtProvider() {
        log.info("[SECRET]: {}", JwtProperties.getSignInKey());
    }

    public String createToken(Long memberId) {
        return createToken(memberId, JwtProperties.TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId, JwtProperties.REFRESH_EXPIRATION);
    }

    private String createToken(Long memberId, long expirationTime) {
        Date date = new Date();

        return JwtProperties.BEARER_PREFIX
                + Jwts.builder()
                        .signWith(JwtProperties.getSignInKey(), SignatureAlgorithm.HS256)
                        .setSubject(String.valueOf(memberId))
                        .setExpiration(new Date(date.getTime() + expirationTime))
                        .setIssuedAt(date)
                        .compact();
    }
}
