package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.jwt.JwtProvider;
import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberRegisterResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final JwtProvider jwtProvider;

    @PostMapping()
    public ResponseEntity<MemberRegisterResponse> createMember(
            @Valid @RequestBody MemberRegisterRequest memberRegisterRequest) {
        Member member = memberService.addMember(memberRegisterRequest);
        String accessToken = jwtProvider.createToken(member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getEmail());
        return ResponseEntity.ok(
                memberMapper.toMemberRegisterResponse(member, accessToken, refreshToken));
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.removeMember(userDetails.id());
        return ResponseEntity.noContent().build();
    }
}
