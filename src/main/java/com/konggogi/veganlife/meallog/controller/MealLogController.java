package com.konggogi.veganlife.meallog.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogAddRequest;
import com.konggogi.veganlife.meallog.controller.dto.request.MealLogModifyRequest;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogDetailsResponse;
import com.konggogi.veganlife.meallog.controller.dto.response.MealLogListResponse;
import com.konggogi.veganlife.meallog.service.MealLogSearchService;
import com.konggogi.veganlife.meallog.service.MealLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-log")
public class MealLogController {

    private final MealLogService mealLogService;
    private final MealLogSearchService mealLogSearchService;

    @PostMapping
    public ResponseEntity<Void> addMealLog(
            @Valid @RequestPart MealLogAddRequest request,
            @RequestPart(required = false) @Size(max = 5) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealLogService.add(request, images, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MealLogListResponse>> getMealLogList(
            @RequestParam LocalDate date, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(mealLogSearchService.searchByDate(date, userDetails.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealLogDetailsResponse> getMealLogDetails(@PathVariable Long id) {

        return ResponseEntity.ok(mealLogSearchService.searchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyMealLog(
            @PathVariable Long id,
            @Valid @RequestPart MealLogModifyRequest request,
            @RequestPart(required = false) @Size(max = 5) List<MultipartFile> images) {

        mealLogService.modify(id, request, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMealLog(@PathVariable Long id) {

        mealLogService.remove(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
