package com.konggogi.veganlife.mealdata.controller;


import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataDetailsResponse;
import com.konggogi.veganlife.mealdata.controller.dto.response.MealDataListResponse;
import com.konggogi.veganlife.mealdata.domain.AccessType;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataDtoMapper;
import com.konggogi.veganlife.mealdata.service.MealDataSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meal-data")
public class MealDataController {

    private final MealDataSearchService mealDataSearchService;
    private final MealDataDtoMapper mealDataDtoMapper;

    @GetMapping
    public ResponseEntity<List<MealDataListResponse>> getMealDataList(
            String keyword, Pageable pageable) {

        return ResponseEntity.ok(
                mealDataSearchService.searchByKeyword(keyword, pageable).stream()
                        .map(mealDataDtoMapper::toMealDataListResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDataDetailsResponse> getMealDataDetails(
            @PathVariable Long id, @RequestParam AccessType accessType) {

        return ResponseEntity.ok(
                mealDataDtoMapper.toMealDataDetailsResponse(
                        mealDataSearchService.search(id, accessType)));
    }
}
