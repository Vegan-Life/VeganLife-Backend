package com.konggogi.veganlife.global.security.handler;


import com.konggogi.veganlife.global.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        /** 핸들링되지 않은 예외는 /error로 redirect, 이를 핸들링하기 위함 */
        if (request.getRequestURI().equals("/error")) {
            resolver.resolveException(request, response, null, new Exception("핸들링 되지 않은 에외입니다."));
        }
        Exception jwtException = (Exception) request.getAttribute(JwtUtils.JWT_EXCEPTION);
        // JWT가 invalid한 경우 || JWT의 user info로 사용자를 찾을 수 없는 경우
        if (jwtException != null) {
            resolver.resolveException(request, response, null, jwtException);
            return;
        }
        // 인증이 없거나 불명확한 이유
        resolver.resolveException(request, response, null, authException);
    }
}
