package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.member.controller.dto.response.ReissueTokenResponse;
import com.konggogi.veganlife.member.domain.mapper.AuthMapper;
import com.konggogi.veganlife.member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper authMapper;

    @PostMapping("/reissue")
    public ResponseEntity<ReissueTokenResponse> reissueToken(
            @RequestAttribute("refreshToken") String refreshToken) {
        String accessToken = refreshTokenService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(authMapper.toReissueTokenResponse(accessToken));
    }
}
