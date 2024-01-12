package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MyPageController {
    private final MemberQueryService memberQueryService;
    private final MemberService memberService;
    private final PostSearchService postSearchService;
    private final MemberMapper memberMapper;
    private final PostMapper postMapper;

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

    @GetMapping("/me/posts")
    public ResponseEntity<Page<PostSimpleResponse>> getMyPostList(
            Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<PostSimpleResponse> postSimpleResponses =
                postSearchService
                        .searchAllSimple(userDetails.id(), pageable)
                        .map(postMapper::toPostSimpleResponse);
        return ResponseEntity.ok(postSimpleResponses);
    }

    @GetMapping("/me/posts-with-comments")
    public ResponseEntity<Page<PostSimpleResponse>> getPostListContainingMyComment(
            Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<PostSimpleResponse> postSimpleResponses =
                postSearchService
                        .searchByMemberComments(userDetails.id(), pageable)
                        .map(postMapper::toPostSimpleResponse);
        return ResponseEntity.ok(postSimpleResponses);
    }
}
