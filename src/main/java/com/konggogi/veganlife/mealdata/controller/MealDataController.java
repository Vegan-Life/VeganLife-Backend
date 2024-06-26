package com.konggogi.veganlife.mealdata.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataUpdateRequest;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import com.konggogi.veganlife.mealdata.service.MealDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-data")
public class MealDataController {

    private final MealDataQueryService mealDataQueryService;
    private final MealDataService mealDataService;
    private final MealDataMapper mealDataMapper;

    @GetMapping
    public ResponseEntity<Page<MealDataListResponse>> getMealDataList(
            String keyword, @RequestParam OwnerType ownerType, Pageable pageable) {

        return ResponseEntity.ok(
                mealDataQueryService
                        .searchByKeyword(keyword, ownerType, pageable)
                        .map(mealDataMapper::toMealDataListResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDataDetailsResponse> getMealDataDetails(@PathVariable Long id) {

        return ResponseEntity.ok(
                mealDataMapper.toMealDataDetailsResponse(mealDataQueryService.search(id)));
    }

    @PostMapping
    public ResponseEntity<Void> addMealData(
            @Valid @RequestBody MealDataUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealDataService.add(request, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMealData(
            @PathVariable("id") Long mealDataId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealDataService.removeById(mealDataId, userDetails.id());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyMealData(
            @PathVariable("id") Long mealDataId,
            @Validated @RequestBody MealDataUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        mealDataService.modifyById(request, mealDataId, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
