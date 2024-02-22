package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.CaloriesOfMealTypeFixture;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.dto.IntakeCalorie;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntakeNutrientsServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock MealLogQueryService mealLogQueryService;
    @Mock MealLogMapper mealLogMapper;
    @InjectMocks IntakeNutrientsService intakeNutrientsService;
    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);
    private final List<MealData> mealData =
            List.of(
                    MealDataFixture.TOTAL_AMOUNT.get(member),
                    MealDataFixture.TOTAL_AMOUNT.get(member));
    private List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
    private List<MealImage> mealImages =
            IntStream.range(0, 2).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
    private List<MealLog> mealLogs;

    @Test
    @DisplayName("일일 섭취량 조회")
    void searchDailyIntakeNutrientsTest() {
        // given
        Meal meal = meals.get(0);
        LocalDate date = LocalDate.of(2024, 02, 15);
        mealLogs = createMealLogs(date);
        int expectedSize = meals.size() * mealLogs.size();

        given(memberQueryService.search(anyLong())).willReturn(member);
        given(mealLogQueryService.searchByDateAndMember(any(LocalDate.class), any(Member.class)))
                .willReturn(mealLogs);
        // when
        IntakeNutrients intakeNutrients =
                intakeNutrientsService.searchDailyIntakeNutrients(member.getId(), date);
        // then
        assertThat(intakeNutrients.calorie()).isEqualTo(meal.getCalorie() * expectedSize);
        assertThat(intakeNutrients.carbs()).isEqualTo(meal.getCarbs() * expectedSize);
        assertThat(intakeNutrients.protein()).isEqualTo(meal.getProtein() * expectedSize);
        assertThat(intakeNutrients.fat()).isEqualTo(meal.getFat() * expectedSize);
    }

    @Test
    @DisplayName("일일 섭취량 조회 - Not Found Member")
    void searchDailyIntakeNutrientsNotFoundMemberTest() {
        // given
        given(memberQueryService.search(anyLong()))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () ->
                                intakeNutrientsService.searchDailyIntakeNutrients(
                                        member.getId(), LocalDate.of(2024, 2, 24)))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(mealLogQueryService)
                .should(never())
                .searchByDateAndMember(any(LocalDate.class), eq(member));
    }

    @Test
    @DisplayName("주간 섭취 칼로리 조회")
    void searchWeeklyIntakeCalorieTest() {
        // given
        LocalDate startDate = LocalDate.of(2023, 2, 18);
        LocalDate endDate = LocalDate.of(2023, 2, 24);
        int totalCalorieOfMealType = meals.get(0).getCalorie() * meals.size();
        Map<MealType, Integer> totalCaloriesOfMealType =
                createCalorieOfMealTypeMap(totalCalorieOfMealType);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(
                        mealLogQueryService.sumCaloriesOfMealTypeByMemberIdAndDate(
                                anyLong(), any(LocalDate.class)))
                .willReturn(new ArrayList<>());
        given(mealLogMapper.toTotalCaloriesOfMealTypeMap(any(List.class)))
                .willReturn(totalCaloriesOfMealType);
        // when
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchWeeklyIntakeCalories(
                        member.getId(), startDate, endDate);
        // then

        assertThat(intakeCalories).hasSize(7);
        assertThat(intakeCalories.get(0).breakfast()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).lunch()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).dinner()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).snack()).isEqualTo(totalCalorieOfMealType * 3);
    }

    @Test
    @DisplayName("칼로리 합산")
    void calcTotalCalorieTest() {
        // given
        List<IntakeCalorie> intakeCalories =
                List.of(
                        CaloriesOfMealTypeFixture.DEFAULT.get(),
                        CaloriesOfMealTypeFixture.DEFAULT.get());
        int expectedCalorie =
                CaloriesOfMealTypeFixture.DEFAULT.get().breakfast() * 4 * intakeCalories.size();
        // when
        int totalCalorie = intakeNutrientsService.calcTotalCalorie(intakeCalories);
        // then
        assertThat(totalCalorie).isEqualTo(expectedCalorie);
    }

    @Test
    @DisplayName("월간 섭취 칼로리 조회")
    void searchMonthlyIntakeCaloriesTest() {
        // given
        LocalDate startDate = LocalDate.of(2023, 2, 18);
        int totalCalorieOfMealType = meals.get(0).getCalorie() * meals.size() * 5;
        Map<MealType, Integer> totalCaloriesOfMealType =
                createCalorieOfMealTypeMap(totalCalorieOfMealType);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(
                        mealLogQueryService.sumCaloriesOfMealTypeByMemberIdAndDateBetween(
                                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(new ArrayList<>());
        given(mealLogMapper.toTotalCaloriesOfMealTypeMap(any(List.class)))
                .willReturn(totalCaloriesOfMealType);
        // when
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchMonthlyIntakeCalories(member.getId(), startDate);
        // then

        assertThat(intakeCalories).hasSize(5);
        assertThat(intakeCalories.get(0).breakfast()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).lunch()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).dinner()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).snack()).isEqualTo(totalCalorieOfMealType * 3);
    }

    @Test
    @DisplayName("연간 섭취 칼로리 조회")
    void searchYearlyIntakeCaloriesTest() {
        // given
        LocalDate startDate = LocalDate.of(2023, 2, 18);
        int totalCalorieOfMealType = meals.get(0).getCalorie() * meals.size() * 5;
        Map<MealType, Integer> totalCaloriesOfMealType =
                createCalorieOfMealTypeMap(totalCalorieOfMealType);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(
                        mealLogQueryService.sumCaloriesOfMealTypeByMemberIdAndDateBetween(
                                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(new ArrayList<>());
        given(mealLogMapper.toTotalCaloriesOfMealTypeMap(any(List.class)))
                .willReturn(totalCaloriesOfMealType);
        // when
        List<IntakeCalorie> intakeCalories =
                intakeNutrientsService.searchYearlyIntakeCalories(member.getId(), startDate);
        // then

        assertThat(intakeCalories).hasSize(12);
        assertThat(intakeCalories.get(0).breakfast()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).lunch()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).dinner()).isEqualTo(totalCalorieOfMealType);
        assertThat(intakeCalories.get(0).snack()).isEqualTo(totalCalorieOfMealType * 3);
    }

    private List<MealLog> createMealLogs(LocalDate date) {
        return List.of(
                MealLogFixture.BREAKFAST.getWithDate(date, meals, mealImages, member),
                MealLogFixture.LUNCH.getWithDate(date, meals, mealImages, member),
                MealLogFixture.DINNER.getWithDate(date, meals, mealImages, member),
                MealLogFixture.BREAKFAST_SNACK.getWithDate(date, meals, mealImages, member),
                MealLogFixture.LUNCH_SNACK.getWithDate(date, meals, mealImages, member),
                MealLogFixture.DINNER_SNACK.getWithDate(date, meals, mealImages, member));
    }

    private Map<MealType, Integer> createCalorieOfMealTypeMap(int totalCalorieOfMealType) {
        Map<MealType, Integer> totalCaloriesOfMealType =
                new HashMap<>() {
                    {
                        put(MealType.BREAKFAST, totalCalorieOfMealType);
                        put(MealType.LUNCH, totalCalorieOfMealType);
                        put(MealType.DINNER, totalCalorieOfMealType);
                        put(MealType.BREAKFAST_SNACK, totalCalorieOfMealType);
                        put(MealType.LUNCH_SNACK, totalCalorieOfMealType);
                        put(MealType.DINNER_SNACK, totalCalorieOfMealType);
                    }
                };
        return totalCaloriesOfMealType;
    }
}
