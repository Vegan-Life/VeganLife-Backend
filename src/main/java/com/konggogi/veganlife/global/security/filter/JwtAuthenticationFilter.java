package com.konggogi.veganlife.global.security.filter;


import com.konggogi.veganlife.global.exception.ApiAuthenticationException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.jwt.JwtPreAuthenticationToken;
import com.konggogi.veganlife.global.security.jwt.JwtProperties;
import com.konggogi.veganlife.global.security.jwt.JwtUtils;
import com.konggogi.veganlife.global.security.provider.JwtAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtProperties.AUTH_TOKEN_HEADER);

        try {
            JwtPreAuthenticationToken jwtToken = getJwtToken(bearerToken);
            /** 추출한 JwtToken을 가지고 인증을 시도한다. */
            Authentication authentication = jwtAuthenticationProvider.authenticate(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ApiAuthenticationException e) {
            request.setAttribute(JwtProperties.EXCEPTION_ATTRIBUTE, e);
        }
        filterChain.doFilter(request, response);
    }

    private JwtPreAuthenticationToken getJwtToken(String bearerToken) {

        return JwtUtils.extractJwtToken(bearerToken)
                .map(JwtPreAuthenticationToken::new)
                .orElseThrow(
                        () ->
                                new InvalidJwtException(
                                        ErrorCode.NOT_FOUND_AUTHORIZATION_CREDENTIALS));
    }
}
