package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.controller.dto.response.CalorieIntakeResponse;
import com.konggogi.veganlife.member.controller.dto.response.DailyIntakeResponse;
import com.konggogi.veganlife.member.controller.dto.response.RecommendNutrientsResponse;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.mapper.NutrientsMapper;
import com.konggogi.veganlife.member.service.IntakeNutrientsService;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.dto.IntakeCalorie;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class NutrientsController {
    private final MemberQueryService memberQueryService;
    private final IntakeNutrientsService intakeNutrientsService;
    private final NutrientsMapper nutrientsMapper;

    @GetMapping("/nutrients")
    public ResponseEntity<RecommendNutrientsResponse> getRecommendNutrients(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = memberQueryService.search(userDetails.id());
        return ResponseEntity.ok(nutrientsMapper.toRecommendNutrientsResponse(member));
    }

    @GetMapping("/nutrients/day")
    public ResponseEntity<DailyIntakeResponse> getDailyIntake(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        IntakeNutrients intakeNutrients =
                intakeNutrientsService.searchDailyIntakeNutrients(userDetails.id(), date);
        return ResponseEntity.ok(nutrientsMapper.toDailyIntakeResponse(intakeNutrients));
    }

    @GetMapping("/nutrients/week")
    public ResponseEntity<CalorieIntakeResponse> getWeeklyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchWeeklyIntakeCalories(
                        userDetails.id(), startDate, endDate);
        return ResponseEntity.ok(nutrientsMapper.toCalorieIntakeResponse(intakeCalories));
    }

    @GetMapping("/nutrients/month")
    public ResponseEntity<CalorieIntakeResponse> getMonthlyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate startDate) {
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchMonthlyIntakeCalories(userDetails.id(), startDate);
        return ResponseEntity.ok(nutrientsMapper.toCalorieIntakeResponse(intakeCalories));
    }

    @GetMapping("/nutrients/year")
    public ResponseEntity<CalorieIntakeResponse> getYearlyIntakeCalorie(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate startDate) {
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchYearlyIntakeCalories(userDetails.id(), startDate);
        return ResponseEntity.ok(nutrientsMapper.toCalorieIntakeResponse(intakeCalories));
    }
}
