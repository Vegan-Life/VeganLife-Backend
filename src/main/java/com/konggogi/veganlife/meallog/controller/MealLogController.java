package com.konggogi.veganlife.meallog.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.service.MealLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-log")
public class MealLogController {

    private final MealLogService mealLogService;

    @PostMapping
    public ResponseEntity<Void> addMealLog(
            @Valid @RequestBody MealLogAddRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealLogService.add(request, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
