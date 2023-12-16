package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.controller.dto.response.MemberInfoResponse;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.controller.dto.response.RecommendNutrientsResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.service.MemberQueryService;
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
    private final MemberQueryService memberQueryService;
    private final MemberMapper memberMapper;

    @PostMapping()
    public ResponseEntity<MemberInfoResponse> modifyMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MemberInfoRequest memberInfoRequest) {
        Member member = memberService.modifyMemberInfo(userDetails.id(), memberInfoRequest);
        return ResponseEntity.ok(memberMapper.toMemberInfoResponse(member));
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.removeMember(userDetails.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> getMemberDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberQueryService.search(userDetails.id());
        return ResponseEntity.ok(memberMapper.toMemberProfileResponse(member));
    }

    @PutMapping("/profile")
    public ResponseEntity<MemberProfileResponse> modifyMemberProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MemberProfileRequest memberProfileRequest) {
        Member member = memberService.modifyMemberProfile(userDetails.id(), memberProfileRequest);
        return ResponseEntity.ok(memberMapper.toMemberProfileResponse(member));
    }

    @GetMapping("/nutrients")
    public ResponseEntity<RecommendNutrientsResponse> getDailyNutrients(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberQueryService.findMemberById(userDetails.id());
        return ResponseEntity.ok(memberMapper.toRecommendNutrientsResponse(member));
    }
}
