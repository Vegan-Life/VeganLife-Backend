package com.konggogi.veganlife.global.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.konggogi.veganlife.global.exception.ApiException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.dto.response.ErrorResponse;
import com.konggogi.veganlife.global.util.LoggingUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

    private final ObjectMapper objectMapper;

    public void handle(HttpServletResponse response, ApiException e) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        LoggingUtils.exceptionLog(HttpStatus.UNAUTHORIZED, e);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter()
                .write(objectMapper.writeValueAsString(ErrorResponse.from(e.getErrorCode())));
        response.getWriter().flush();
    }

    public void handle(HttpServletResponse response, Exception e) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        LoggingUtils.exceptionLog(HttpStatus.UNAUTHORIZED, e.getCause());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                ErrorResponse.from(
                                        ErrorCode.UNHANDLED_ERROR, buildExceptionMessage(e))));
        response.getWriter().flush();
    }

    private String buildExceptionMessage(Exception e) {

        Throwable cause = e.getCause();
        return String.format(
                "%s\nCause: %s, Message: %s",
                ErrorCode.UNHANDLED_ERROR.getDescription(),
                cause.getClass().getSimpleName(),
                cause.getMessage());
    }
}
