package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.JwtUserPrincipal;
import com.konggogi.veganlife.member.controller.dto.request.OauthLoginRequest;
import com.konggogi.veganlife.member.controller.dto.request.SignupRequest;
import com.konggogi.veganlife.member.controller.dto.response.JwtTokenResponse;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberMapper memberMapper;

    @PostMapping("/{provider}/login")
    public ResponseEntity<JwtTokenResponse> login(
            @PathVariable OauthProvider provider, @RequestBody OauthLoginRequest request) {

        return authService
                .login(provider, request)
                .map(jwtToken -> ResponseEntity.ok(memberMapper.toJwtTokenResponse(jwtToken)))
                .orElseGet(() -> ResponseEntity.accepted().build());
    }

    @PostMapping("/{provider}/signup")
    public ResponseEntity<JwtTokenResponse> signup(
            @PathVariable OauthProvider provider, @RequestBody @Valid SignupRequest request) {

        return ResponseEntity.ok(
                memberMapper.toJwtTokenResponse(authService.signup(provider, request)));
    }

    /** Refresh Token이 유효한 경우에만 해당 요청을 수행할 수 있다. */
    /** TODO: principal에 refresh token이 담기도록 수정 -> @AuthenticationPrincipal 어노테이션 재정의 */
    @PostMapping("/reissue")
    public ResponseEntity<JwtTokenResponse> reissueToken(
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        return ResponseEntity.ok(
                memberMapper.toJwtTokenResponse(authService.reissueToken(principal.id())));
    }
}
