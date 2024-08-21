package com.konggogi.veganlife.global.security.filter;


import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.security.handler.AuthenticationExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthenticationExceptionTranslationFilter extends OncePerRequestFilter {

    private final AuthenticationExceptionHandler authenticationExceptionHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            doFilter(request, response, filterChain);
        } catch (ApiException e) {
            authenticationExceptionHandler.handle(response, e);
        } catch (Exception e) {
            authenticationExceptionHandler.handle(response, e);
        }
    }
}
