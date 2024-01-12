package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.member.controller.dto.request.ReissueRequest;
import com.konggogi.veganlife.member.controller.dto.response.AuthResponse;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper authMapper;

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> reissueToken(@RequestBody ReissueRequest reissueRequest) {
        String refreshToken = reissueRequest.refreshToken();
        String accessToken = refreshTokenService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(authMapper.toAuthResponse(accessToken));
    }
}
