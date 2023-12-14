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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String extractToken = request.getHeader(JwtUtils.AUTH_TOKEN_HEADER);
        jwtUtils.extractBearerToken(extractToken)
                .ifPresent(
                        jwt -> {
                            jwtUtils.validateToken(jwt);
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                String email = getUserEmailFromJwt(jwt);
                                UserDetails userDetails =
                                        userDetailsServiceImpl.loadUserByUsername(email);
                                setAuthenticationInSecurityContext(request, userDetails);
                            }
                        });
        filterChain.doFilter(request, response);
    }

    private String getUserEmailFromJwt(String token) {
        return jwtUtils.extractUserEmail(token)
                .orElseThrow(() -> new InvalidJwtException(ErrorCode.NOT_FOUND_USER_INFO_TOKEN));
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
