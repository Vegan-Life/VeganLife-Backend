package com.konggogi.veganlife.global.security.jwt;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.user.JwtUserPrincipal;
import io.jsonwebtoken.*;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtUtils {

    public static Optional<String> extractJwtToken(String token) {

        if (StringUtils.hasText(token) && token.startsWith(JwtProperties.BEARER_PREFIX)) {
            return Optional.of(token.substring(JwtProperties.BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    public static JwtUserPrincipal getPrincipal(String token) {
        validateToken(token);
        return new JwtUserPrincipal(extractClaim(token, Claims::getSubject));
    }

    private String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private static void validateToken(String token) {
        try {
            extractAllClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            throw new InvalidJwtException(ErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new InvalidJwtException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            // token claim is blank or token is null
            throw new InvalidJwtException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.warn("처리되지 않은 JWT 오류입니다.");
            throw new InvalidJwtException(ErrorCode.UNEXPECTED_TOKEN);
        }
    }

    private static <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JwtProperties.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
