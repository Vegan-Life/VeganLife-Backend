package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.controller.dto.response.AuthResponse;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final MemberService memberService;
    private final JwtUtils jwtUtils;
    private final AuthMapper authMapper;

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String accessToken =
                jwtUtils.extractTokenFromHeader(request)
                        .map(token -> memberService.reissueToken(token, userDetails.id()))
                        .orElseThrow(
                                () -> {
                                    throw new InvalidJwtException(ErrorCode.INVALID_TOKEN);
                                });
        return ResponseEntity.ok(authMapper.toAuthResponse(accessToken));
    }
}
