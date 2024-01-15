package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.AdditionalInfoUpdateRequest;
import com.konggogi.veganlife.member.controller.dto.response.AdditionalInfoUpdateResponse;
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

    @PostMapping()
    public ResponseEntity<AdditionalInfoUpdateResponse> modifyAdditionalInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AdditionalInfoUpdateRequest additionalInfoUpdateRequest) {
        Member member =
                memberService.updateAdditionalInfo(userDetails.id(), additionalInfoUpdateRequest);
        return ResponseEntity.ok(memberMapper.toAdditionalInfoUpdateResponse(member));
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.removeMember(userDetails.id());
        return ResponseEntity.noContent().build();
    }
}
