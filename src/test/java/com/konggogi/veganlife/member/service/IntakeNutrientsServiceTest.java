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
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.CaloriesOfMealTypeFixture;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.dto.CaloriesOfMealType;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.*;
import java.util.List;
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
    @Mock MealLogRepository mealLogRepository;
    @Mock MealLogQueryService mealLogQueryService;
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
                                        member.getId(), any(LocalDate.class)))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
        then(mealLogQueryService)
                .should(never())
                .searchByDateAndMember(any(LocalDate.class), eq(member));
    }

    @Test
    @DisplayName("주간 섭취량 조회")
    void searchWeeklyIntakeCalorieTest() {
        // given
        Long memberId = member.getId();
        Meal meal = meals.get(0);
        LocalDate startDate = LocalDate.of(2023, 12, 17);
        LocalDate endDate = LocalDate.of(2023, 12, 23);
        List<MealLog> mealLogs = createMealLogs(startDate);
        given(memberQueryService.search(memberId)).willReturn(member);
        given(
                        mealLogRepository.findAllByMemberIdAndCreatedAtBetween(
                                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(mealLogs);
        // when
        List<CaloriesOfMealType> caloriesOfMealTypes =
                intakeNutrientsService.searchWeeklyIntakeCalories(memberId, startDate, endDate);
        // then
        assertThat(caloriesOfMealTypes).hasSize(7);
        assertThat(caloriesOfMealTypes.get(0).breakfast())
                .isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).lunch()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).dinner()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).snack())
                .isEqualTo(meal.getCalorie() * meals.size() * 3);
    }

    @Test
    @DisplayName("주간 섭취량 조회시 없는 회원이면 예외 발생")
    void searchWeeklyIntakeNutrientsNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () ->
                                intakeNutrientsService.searchWeeklyIntakeCalories(
                                        memberId, LocalDate.now(), LocalDate.now()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
    }

    @Test
    @DisplayName("총 섭취한 칼로리 계산")
    void calcTotalCalorieTest() {
        // given
        List<CaloriesOfMealType> caloriesOfMealTypes =
                List.of(
                        CaloriesOfMealTypeFixture.DEFAULT.get(),
                        CaloriesOfMealTypeFixture.DEFAULT.get());
        int expectedCalorie =
                CaloriesOfMealTypeFixture.DEFAULT.get().breakfast()
                        * 4
                        * caloriesOfMealTypes.size();
        // when
        int totalCalorie = intakeNutrientsService.calcTotalCalorie(caloriesOfMealTypes);
        // then
        assertThat(totalCalorie).isEqualTo(expectedCalorie);
    }

    @Test
    @DisplayName("월간 섭취량 조회")
    void searchMonthlyIntakeCaloriesTest() {
        // given
        Long memberId = member.getId();
        Meal meal = meals.get(0);
        List<MealLog> mealLogs = createMealLogs(LocalDate.now());
        given(memberQueryService.search(memberId)).willReturn(member);
        given(
                        mealLogRepository.findAllByMemberIdAndCreatedAtBetween(
                                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(mealLogs);
        // when
        List<CaloriesOfMealType> caloriesOfMealTypes =
                intakeNutrientsService.searchMonthlyIntakeCalories(
                        memberId, LocalDate.of(2023, 12, 1));
        // then
        assertThat(caloriesOfMealTypes.get(0).breakfast())
                .isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).lunch()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).dinner()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).snack())
                .isEqualTo(meal.getCalorie() * meals.size() * 3);
    }

    @Test
    @DisplayName("월간 섭취량 조회 시 없는 회원 예외 발생")
    void searchMonthlyIntakeCaloriesNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () ->
                                intakeNutrientsService.searchMonthlyIntakeCalories(
                                        memberId, LocalDate.now()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
    }

    @Test
    @DisplayName("연간 섭취량 조회")
    void searchYearlyIntakeCaloriesTest() {
        // given
        Long memberId = member.getId();
        Meal meal = meals.get(0);
        List<MealLog> mealLogs = createMealLogs(LocalDate.now());
        given(memberQueryService.search(memberId)).willReturn(member);
        given(
                        mealLogRepository.findAllByMemberIdAndCreatedAtBetween(
                                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(mealLogs);
        // when
        List<CaloriesOfMealType> caloriesOfMealTypes =
                intakeNutrientsService.searchYearlyIntakeCalories(
                        memberId, LocalDate.of(2023, 1, 1));
        // then
        assertThat(caloriesOfMealTypes.get(0).breakfast())
                .isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).lunch()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).dinner()).isEqualTo(meal.getCalorie() * meals.size());
        assertThat(caloriesOfMealTypes.get(0).snack())
                .isEqualTo(meal.getCalorie() * meals.size() * 3);
    }

    @Test
    @DisplayName("연간 섭취량 조회 시 없는 회원 예외 발생")
    void searchYearlyIntakeCaloriesNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () ->
                                intakeNutrientsService.searchYearlyIntakeCalories(
                                        memberId, LocalDate.now()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
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
}
