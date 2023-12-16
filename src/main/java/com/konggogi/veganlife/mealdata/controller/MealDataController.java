package com.konggogi.veganlife.mealdata.controller;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.service.MealDataQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-data")
public class MealDataController {

    private final MealDataQueryService mealDataQueryService;
    private final MealDataMapper mealDataMapper;

    @GetMapping
    public ResponseEntity<List<MealDataListResponse>> getMealDataList(
            String keyword, Pageable pageable) {

        return ResponseEntity.ok(
                mealDataQueryService.searchByKeyword(keyword, pageable).stream()
                        .map(mealDataMapper::toMealDataListResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDataDetailsResponse> getMealDataDetails(@PathVariable Long id) {

        return ResponseEntity.ok(
                mealDataMapper.toMealDataDetailsResponse(mealDataQueryService.search(id)));
    }
}
