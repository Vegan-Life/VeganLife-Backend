package com.konggogi.veganlife.mealdata.domain;


import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalMealData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_meal_data_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MealDataType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessType accessType;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public PersonalMealData(
            Long id,
            String name,
            MealDataType type,
            Integer amount,
            Integer amountPerServe,
            Double caloriePerUnit,
            Double proteinPerUnit,
            Double fatPerUnit,
            Double carbsPerUnit,
            IntakeUnit intakeUnit,
            Member member) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.accessType = AccessType.PERSONAL;
        this.amount = amount;
        this.amountPerServe = amountPerServe;
        this.caloriePerUnit = caloriePerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.fatPerUnit = fatPerUnit;
        this.carbsPerUnit = carbsPerUnit;
        this.intakeUnit = intakeUnit;
        this.member = member;
    }
}
