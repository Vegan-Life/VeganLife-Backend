package com.konggogi.veganlife.meallog.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "meal_log",
        indexes = {
            @Index(name = "idx_meal_log_on_member_created_at", columnList = "member_id, createdAt")
        })
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

    public void modifyMeals(List<Meal> meals) {
        this.meals.clear();
        this.meals.addAll(meals);
    }

    public void modifyMealImages(List<MealImage> mealImages) {
        this.mealImages.clear();
        this.mealImages.addAll(mealImages);
    }

    public MealImage getThumbnail() {

        if (mealImages.isEmpty()) {
            return null;
        }
        return mealImages.get(0);
    }

    public int getTotalCalorie() {
        return calculateTotal(Meal::getCalorie, meals);
    }

    public IntakeNutrients getTotalIntakeNutrients() {
        // TODO: reduce를 사용했을 때와 성능 비교해보기
        return new IntakeNutrients(
                calculateTotal(Meal::getCalorie, meals),
                calculateTotal(Meal::getCarbs, meals),
                calculateTotal(Meal::getProtein, meals),
                calculateTotal(Meal::getFat, meals));
    }

    private int calculateTotal(ToIntFunction<Meal> func, List<Meal> meals) {

        return meals.stream().mapToInt(func).sum();
    }
}
