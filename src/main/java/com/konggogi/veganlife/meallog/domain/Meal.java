package com.konggogi.veganlife.meallog.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.mealdata.domain.IntakeUnit;
import com.konggogi.veganlife.mealdata.domain.MealData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer intake;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IntakeUnit intakeUnit;

    @Column(nullable = false)
    private Integer calorie;

    @Column(nullable = false)
    private Integer carbs;

    @Column(nullable = false)
    private Integer protein;

    @Column(nullable = false)
    private Integer fat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_log_id")
    private MealLog mealLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_data_id")
    private MealData mealData;

    @Builder
    public Meal(
            Long id,
            String name,
            Integer intake,
            IntakeUnit intakeUnit,
            Integer calorie,
            Integer carbs,
            Integer protein,
            Integer fat,
            MealLog mealLog,
            MealData mealData) {
        this.id = id;
        this.name = name;
        this.intake = intake;
        this.intakeUnit = intakeUnit;
        this.calorie = calorie;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.mealLog = mealLog;
        this.mealData = mealData;
    }
}
