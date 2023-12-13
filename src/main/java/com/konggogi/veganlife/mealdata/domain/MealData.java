package com.konggogi.veganlife.mealdata.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealData extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_data_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MealDataType type;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer amountPerServe;

    @Column(nullable = false)
    private Double calorie;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double fat;

    @Column(nullable = false)
    private Double carbs;

    @Column(nullable = false)
    private Double caloriePerGram;

    @Column(nullable = false)
    private Double proteinPerGram;

    @Column(nullable = false)
    private Double fatPerGram;

    @Column(nullable = false)
    private Double carbsPerGram;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IntakeUnit intakeUnit;

    @Builder
    public MealData(
            Long id,
            String name,
            MealDataType type,
            Integer amount,
            Integer amountPerServe,
            Double calorie,
            Double protein,
            Double fat,
            Double carbs,
            Double caloriePerGram,
            Double proteinPerGram,
            Double fatPerGram,
            Double carbsPerGram,
            IntakeUnit intakeUnit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.calorie = calorie;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.caloriePerGram = caloriePerGram;
        this.proteinPerGram = proteinPerGram;
        this.fatPerGram = fatPerGram;
        this.carbsPerGram = carbsPerGram;
        this.intakeUnit = intakeUnit;
    }
}
