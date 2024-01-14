package com.konggogi.veganlife.global.security.interceptor;


import com.konggogi.veganlife.global.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ReissueInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String bearerToken = request.getHeader(JwtUtils.AUTH_TOKEN_HEADER);
        String refreshToken = jwtUtils.extractBearerToken(bearerToken).get();
        request.setAttribute("refreshToken", refreshToken);
        return true;
    }
}
