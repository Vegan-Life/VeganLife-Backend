package com.konggogi.veganlife.global.security.jwt;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {

    public static final String AUTH_TOKEN_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String EXCEPTION_ATTRIBUTE = "exception";
    public static long TOKEN_EXPIRATION;
    public static long REFRESH_EXPIRATION;
    private static String SECRET_KEY;

    @Value("${jwt.expiration}")
    public void setTokenExpiration(String value) {
        TOKEN_EXPIRATION = Long.parseLong(value);
    }

    @Value("${jwt.refresh-token.expiration}")
    public void setRefreshExpiration(String value) {
        REFRESH_EXPIRATION = Long.parseLong(value);
    }

    @Value("${jwt.secret-key}")
    public void setSecretKey(String value) {
        SECRET_KEY = value;
    }

    public static Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
