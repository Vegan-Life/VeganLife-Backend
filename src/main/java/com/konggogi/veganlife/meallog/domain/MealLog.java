package com.konggogi.veganlife.meallog.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealLog extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_log_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @OneToMany(mappedBy = "mealLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals = new ArrayList<>();

    @OneToMany(mappedBy = "mealLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealImage> mealImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MealLog(Long id, MealType mealType, Member member) {
        this.id = id;
        this.mealType = mealType;
        this.member = member;
    }

    public void addMeals(List<Meal> meals) {
        this.meals.addAll(meals);
    }

    public void addMealImages(List<MealImage> mealImages) {
        this.mealImages.addAll(mealImages);
    }

    public void updateMeals(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
    }

    public void updateMealImages(List<MealImage> mealImages) {
        this.mealImages.clear();
        this.mealImages.addAll(mealImages);
    }
}
