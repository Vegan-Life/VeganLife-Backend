package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.ProfileModifyRequest;
import com.konggogi.veganlife.member.controller.dto.response.MemberProfileResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostSearchService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MyPageController {
    private final MemberQueryService memberQueryService;
    private final MemberService memberService;
    private final PostSearchService postSearchService;
    private final RecipeSearchService recipeSearchService;
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
            @RequestPart @Valid ProfileModifyRequest request,
            @RequestPart(required = false) MultipartFile image) {
        Member member = memberService.modifyProfile(userDetails.id(), request, image);
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

    @GetMapping("/me/liked-recipes")
    public ResponseEntity<Page<RecipeResponse>> getLikedRecipes(
            Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(
                recipeSearchService.searchLikedRecipes(userDetails.id(), pageable));
    }
}
