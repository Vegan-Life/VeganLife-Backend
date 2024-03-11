package com.konggogi.veganlife.member.service.dto;

public record IntakeNutrients(Integer calorie, Integer carbs, Integer protein, Integer fat) {
    public IntakeNutrients add(IntakeNutrients other) {
        return new IntakeNutrients(
                this.calorie + other.calorie,
                this.carbs + other.carbs,
                this.protein + other.protein,
                this.fat + other.fat);
    }
}
