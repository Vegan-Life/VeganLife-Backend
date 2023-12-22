package com.konggogi.veganlife.global.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute(JwtUtils.EXCEPTION_ATTRIBUTE);
        log.error(
                "[Exception] - JwtAuthenticationEntryPoint.commence - uri: {} - {}",
                request.getRequestURI(),
                errorCode);
        setErrorResponse(response, errorCode);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        String result = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
