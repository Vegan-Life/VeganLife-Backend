package com.konggogi.veganlife.meallog.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-log")
public class MealLogController {

    private final MealLogService mealLogService;
    private final MealLogQueryService mealLogQueryService;
    private final MealLogMapper mealLogMapper;

    @PostMapping
    public ResponseEntity<Void> addMealLog(
            @Valid @RequestBody MealLogAddRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealLogService.add(request, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MealLogListResponse>> getMealLogList(
            @RequestParam LocalDate date, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(
                mealLogQueryService.searchByDate(date, userDetails.id()).stream()
                        .map(mealLogMapper::toMealLogListResponse)
                        .toList());
    }
}
