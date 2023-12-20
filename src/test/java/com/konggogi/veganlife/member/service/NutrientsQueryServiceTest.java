package com.konggogi.veganlife.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NutrientsQueryServiceTest {
    @Mock MemberQueryService memberQueryService;
    @Mock MealLogRepository mealLogRepository;
    @InjectMocks NutrientsQueryService nutrientsQueryService;
    private final Member member = MemberFixture.DEFAULT_M.getMember();
    private final List<MealData> mealData =
            List.of(MealDataFixture.MEAL.get(member), MealDataFixture.MEAL.get(member));
    private List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
    private List<MealLog> mealLogs;

    @Test
    @DisplayName("금일 섭취량 조회")
    void searchDailyIntakeNutrientsTest() {
        // given
        Long memberId = member.getId();
        Meal meal = meals.get(0);
        mealLogs =
                List.of(
                        MealLogFixture.BREAKFAST.getWithDate(meals, member, LocalDate.now()),
                        MealLogFixture.LUNCH.getWithDate(meals, member, LocalDate.now()));
        int expectedSize = meals.size() * mealLogs.size();
        LocalDate date = mealLogs.get(0).getModifiedAt().toLocalDate();
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        given(memberQueryService.search(memberId)).willReturn(member);
        given(mealLogRepository.findAllByMemberIdAndModifiedAtBetween(memberId, startDate, endDate))
                .willReturn(mealLogs);
        // when
        IntakeNutrients intakeNutrients =
                nutrientsQueryService.searchDailyIntakeNutrients(memberId, date);
        // then
        assertThat(intakeNutrients.calorie()).isEqualTo(meal.getCalorie() * expectedSize);
        assertThat(intakeNutrients.carbs()).isEqualTo(meal.getCarbs() * expectedSize);
        assertThat(intakeNutrients.protein()).isEqualTo(meal.getProtein() * expectedSize);
        assertThat(intakeNutrients.fat()).isEqualTo(meal.getFat() * expectedSize);
    }

    @Test
    @DisplayName("금일 섭취량 조회시 없는 회원이면 예외 발생")
    void searchDailyIntakeNutrientsNotMemberTest() {
        // given
        Long memberId = member.getId();
        given(memberQueryService.search(memberId))
                .willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEMBER));
        // when, then
        assertThatThrownBy(
                        () ->
                                nutrientsQueryService.searchDailyIntakeNutrients(
                                        memberId, LocalDate.now()))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getDescription());
    }
}
