package com.konggogi.veganlife.global.jwt;


import com.konggogi.veganlife.global.util.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final long TOKEN_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private Key key;
    private final JwtUtils jwtUtils;

    public JwtProvider(
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-token.expiration}") long refreshExpiration,
            JwtUtils jwtUtils) {
        this.TOKEN_EXPIRATION = expiration * 1000L;
        this.REFRESH_EXPIRATION = refreshExpiration * 1000L;
        this.jwtUtils = jwtUtils;
    }

    @PostConstruct
    public void init() {
        key = jwtUtils.getSignInKey();
    }

    public String createToken(String email) {
        return createToken(email, TOKEN_EXPIRATION);
    }

    public String createRefreshToken(String email) {
        return createToken(email, REFRESH_EXPIRATION);
    }

    private String createToken(String email, long expirationTime) {
        Date date = new Date();

        return TOKEN_PREFIX
                + Jwts.builder()
                        .signWith(key, SignatureAlgorithm.HS256)
                        .setSubject(email)
                        .setExpiration(new Date(date.getTime() + expirationTime))
                        .setIssuedAt(date)
                        .compact();
    }
}
