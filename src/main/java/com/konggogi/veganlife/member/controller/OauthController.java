package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.member.controller.dto.request.OauthRequest;
import com.konggogi.veganlife.member.controller.dto.response.OauthLoginResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.oauth.OauthProvider;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.member.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members/oauth")
public class OauthController {
    private final OauthService oauthService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final MemberMapper memberMapper;

    @PostMapping("/{provider}/login")
    public ResponseEntity<OauthLoginResponse> login(
            @PathVariable OauthProvider provider, @RequestBody OauthRequest oauthRequest) {
        Member member = oauthService.createMemberFromToken(provider, oauthRequest.accessToken());
        Member savedMember = memberService.addMember(member.getEmail());
        String accessToken = jwtProvider.createToken(savedMember.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(savedMember.getEmail());
        memberService.saveRefreshToken(savedMember.getId(), refreshToken);
        return ResponseEntity.ok(
                memberMapper.toOauthLoginResponse(savedMember, accessToken, refreshToken));
    }
}
