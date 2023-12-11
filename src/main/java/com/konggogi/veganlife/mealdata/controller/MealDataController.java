package com.konggogi.veganlife.mealdata.controller;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-data")
public class MealDataController {

    private final MealDataQueryService mealDataQueryService;
    private final MealDataMapper mealDataMapper;

    @GetMapping
    public ResponseEntity<Page<MealDataListResponse>> getMealDataList(
            String keyword, Pageable pageable) {

        return ResponseEntity.ok(
                mealDataQueryService
                        .searchByKeyword(keyword, pageable)
                        .map(mealDataMapper::toMealDataListResponse));
    }
}
