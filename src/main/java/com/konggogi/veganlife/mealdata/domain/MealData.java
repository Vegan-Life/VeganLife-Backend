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
    private Double caloriePerUnit;

    @Column(nullable = false)
    private Double proteinPerUnit;

    @Column(nullable = false)
    private Double fatPerUnit;

    @Column(nullable = false)
    private Double carbsPerUnit;

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
            Double caloriePerUnit,
            Double proteinPerUnit,
            Double fatPerUnit,
            Double carbsPerUnit,
            IntakeUnit intakeUnit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.caloriePerUnit = caloriePerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.fatPerUnit = fatPerUnit;
        this.carbsPerUnit = carbsPerUnit;
        this.intakeUnit = intakeUnit;
    }
}
