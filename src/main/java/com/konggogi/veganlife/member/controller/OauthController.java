package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.controller.dto.request.OauthRequest;
import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members/oauth")
public class OauthController {
    private final OauthService oauthService;

    @PostMapping("/{provider}/login")
    public ResponseEntity<OauthLoginResponse> login(
            @PathVariable OauthProvider provider, @RequestBody OauthRequest oauthRequest) {
        Member member = oauthService.createMemberFromToken(provider, oauthRequest.accessToken());
        return ResponseEntity.ok(OauthLoginResponse.from(member));
    }
}
