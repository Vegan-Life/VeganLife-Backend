package com.konggogi.veganlife.global.security.filter;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.user.UserDetailsServiceImpl;
import com.konggogi.veganlife.global.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtUtils.AUTH_TOKEN_HEADER);

        /**
         * 헤더에 Bearer Token이 있는 경우에만 해당 로직 실행 만약 없다면 일단 Filter 계속 수행 -> 인증이 필요한 uri일 경우
         * Authentication이 존재하지 않으면 unauthroized 응답 반환
         */
        try {
            jwtUtils.extractBearerToken(bearerToken)
                    .ifPresent(
                            jwt -> {
                                jwtUtils.validateToken(jwt);
                                setAuthentication(request, jwt);
                            });
        } catch (InvalidJwtException | UsernameNotFoundException e) {
            request.setAttribute(JwtUtils.JWT_EXCEPTION, e);
        }
        filterChain.doFilter(request, response);
    }

    private String getUserEmailFromJwt(String token) {
        return jwtUtils.extractUserEmail(token)
                .orElseThrow(() -> new InvalidJwtException(ErrorCode.NOT_FOUND_USER_INFO_TOKEN));
    }

    private void setAuthentication(HttpServletRequest request, String jwt) {
        getAuthentication()
                .ifPresentOrElse(
                        (auth) -> {},
                        () -> {
                            String email = getUserEmailFromJwt(jwt);
                            UserDetails userDetails =
                                    userDetailsServiceImpl.loadUserByUsername(email);
                            setAuthenticationInSecurityContext(request, userDetails);
                        });
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    private void setAuthenticationInSecurityContext(
            HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
