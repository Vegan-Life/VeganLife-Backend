package com.konggogi.veganlife.member.service.dto;

public record IntakeNutrients(Integer calorie, Integer carbs, Integer protein, Integer fat) {
    public static IntakeNutrients combine(IntakeNutrients a, IntakeNutrients b) {
        return new IntakeNutrients(
                a.calorie() + b.calorie(),
                a.carbs() + b.carbs(),
                a.protein() + b.protein(),
                a.fat() + b.fat());
    }
}
