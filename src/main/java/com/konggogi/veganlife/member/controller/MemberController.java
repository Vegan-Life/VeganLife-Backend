package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.request.MemberInfoRequest;
import com.konggogi.veganlife.member.controller.dto.request.MemberProfileRequest;
import com.konggogi.veganlife.member.controller.dto.response.*;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.MemberMapper;
import com.konggogi.veganlife.member.domain.mapper.NutrientsMapper;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.member.service.NutrientsQueryService;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostSearchService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final NutrientsQueryService nutrientsQueryService;
    private final PostSearchService postSearchService;
    private final MemberMapper memberMapper;
    private final NutrientsMapper nutrientsMapper;
    private final PostMapper postMapper;

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
    public ResponseEntity<RecommendNutrientsResponse> getRecommendNutrients(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberQueryService.search(userDetails.id());
        return ResponseEntity.ok(memberMapper.toRecommendNutrientsResponse(member));
    }

    @GetMapping("/nutrients/today")
    public ResponseEntity<TodayIntakeResponse> getTodayIntake(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        IntakeNutrients intakeNutrients =
                nutrientsQueryService.searchDailyIntakeNutrients(userDetails.id(), date);
        return ResponseEntity.ok(nutrientsMapper.toTodayIntakeResponse(intakeNutrients));
    }

    @GetMapping("/nutrients/week")
    public ResponseEntity<CalorieIntakeResponse> getWeeklyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<CaloriesOfMealType> mealCalories =
                nutrientsQueryService.searchWeeklyIntakeCalories(
                        userDetails.id(), startDate, endDate);
        int totalCalorie = nutrientsQueryService.calcTotalCalorie(mealCalories);
        return ResponseEntity.ok(
                nutrientsMapper.toCalorieIntakeResponse(totalCalorie, mealCalories));
    }

    @GetMapping("/nutrients/month")
    public ResponseEntity<CalorieIntakeResponse> getMonthlyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate startDate) {
        List<CaloriesOfMealType> mealCalories =
                nutrientsQueryService.searchMonthlyIntakeCalories(userDetails.id(), startDate);
        int totalCalorie = nutrientsQueryService.calcTotalCalorie(mealCalories);
        return ResponseEntity.ok(
                nutrientsMapper.toCalorieIntakeResponse(totalCalorie, mealCalories));
    }

    @GetMapping("/nutrients/year")
    public ResponseEntity<CalorieIntakeResponse> getYearlyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate startDate) {
        List<CaloriesOfMealType> mealCalories =
                nutrientsQueryService.searchYearlyIntakeCalories(userDetails.id(), startDate);
        int totalCalorie = nutrientsQueryService.calcTotalCalorie(mealCalories);
        return ResponseEntity.ok(
                nutrientsMapper.toCalorieIntakeResponse(totalCalorie, mealCalories));
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
}
