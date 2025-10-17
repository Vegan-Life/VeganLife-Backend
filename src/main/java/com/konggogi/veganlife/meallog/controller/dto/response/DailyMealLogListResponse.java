package com.konggogi.veganlife.meallog.controller.dto.response;


import com.konggogi.veganlife.meallog.domain.MealLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DailyMealLogListResponse(LocalDate date, List<MealLogListResponse> mealLogs) {

    public static DailyMealLogListResponse from(Map.Entry<LocalDate, List<MealLog>> entry) {
        return new DailyMealLogListResponse(
                entry.getKey(), entry.getValue().stream().map(MealLogListResponse::from).toList());
    }
}
