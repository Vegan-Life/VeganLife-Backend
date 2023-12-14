package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.security.exception.InvalidJwtException;
import com.konggogi.veganlife.global.util.JwtUtils;
import com.konggogi.veganlife.member.controller.dto.request.ReissueRequest;
import com.konggogi.veganlife.member.controller.dto.response.AuthResponse;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final MemberQueryService memberQueryService;
    private final JwtUtils jwtUtils;
    private final AuthMapper authMapper;

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody ReissueRequest reissueRequest) {
        String refreshToken = reissueRequest.refreshToken();
        String accessToken =
                jwtUtils.extractBearerToken(refreshToken)
                        .map(memberQueryService::reissueToken)
                        .orElseThrow(
                                () -> {
                                    throw new InvalidJwtException(ErrorCode.INVALID_TOKEN);
                                });
        return ResponseEntity.ok(authMapper.toAuthResponse(accessToken));
    }
}
