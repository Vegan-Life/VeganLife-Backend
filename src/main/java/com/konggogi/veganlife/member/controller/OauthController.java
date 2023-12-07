package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.member.dto.request.OauthRequest;
import com.konggogi.veganlife.member.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members/oauth")
public class OauthController {
    private final OauthService oauthService;

    @PostMapping("/login/kakao")
    public ResponseEntity<OauthLoginResponse> login(@RequestBody OauthRequest oauthRequest) {
        OauthLoginResponse oauthLoginResponse =
                oauthService.loginWithToken(oauthRequest.accessToken());
        return ResponseEntity.ok(oauthLoginResponse);
    }
}
