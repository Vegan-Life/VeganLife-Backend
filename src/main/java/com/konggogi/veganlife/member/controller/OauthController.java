package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.dto.request.OauthRequest;
import com.konggogi.veganlife.member.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members/oauth")
public class OauthController {
    private final OauthService oauthService;
    private final JwtProvider jwtProvider;

    @PostMapping("/{provider}/login")
    public ResponseEntity<OauthLoginResponse> login(
            @PathVariable String provider, @RequestBody OauthRequest oauthRequest) {
        Member member = oauthService.createMemberFromToken(provider, oauthRequest.accessToken());
        boolean isSigned = oauthService.isSignedMember(member);
        String accessToken = jwtProvider.createToken(member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getEmail());
        return ResponseEntity.ok(OauthLoginResponse.from(isSigned, accessToken, refreshToken));
    }
}
