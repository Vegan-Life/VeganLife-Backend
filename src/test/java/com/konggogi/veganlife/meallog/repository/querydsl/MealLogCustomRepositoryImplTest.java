package com.konggogi.veganlife.meallog.repository.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.domain.MealType;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.meallog.repository.MealImageRepository;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.meallog.repository.MealRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.member.service.dto.TotalCalorieOfMealType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing(setDates = false)
class MealLogCustomRepositoryImplTest {
    @Autowired MealLogRepository mealLogRepository;
    @Autowired MealRepository mealRepository;
    @Autowired MealImageRepository mealImageRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MealDataRepository mealDataRepository;

    private final Member member = MemberFixture.DEFAULT_M.get();

    List<MealData> mealData =
            List.of(
                    MealDataFixture.TOTAL_AMOUNT.get(member),
                    MealDataFixture.TOTAL_AMOUNT.get(member),
                    MealDataFixture.TOTAL_AMOUNT.get(member));

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        mealDataRepository.saveAll(mealData);
    }

    @Test
    @DisplayName("회원 id와 기간에 해당하는 MealLog를 MealType별로 합산")
    void sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetweenTest() {
        // given
        LocalDate startDate = LocalDate.of(2024, 2, 18);
        LocalDate endDate = LocalDate.of(2024, 2, 24);
        startDate
                .datesUntil(endDate.plusDays(1))
                .forEach(
                        date -> {
                            Arrays.stream(MealLogFixture.values())
                                    .forEach(
                                            mealType -> {
                                                MealLog mealLog =
                                                        createMealLog(
                                                                member, mealData, date, mealType);
                                                mealLogRepository.save(mealLog);
                                            });
                        });
        // when

        List<TotalCalorieOfMealType> totalCaloriesOfMealTypes =
                mealLogRepository.sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetween(
                        member.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        Map<MealType, Integer> totalCalorieOfMealTypeMap =
                toTotalCalorieOfMealTypeMap(totalCaloriesOfMealTypes);
        // then
        int expectedCalorie =
                MealFixture.DEFAULT.get(mealData.get(0)).getCalorie() * mealData.size() * 7;
        assertThat(totalCalorieOfMealTypeMap.get(MealType.BREAKFAST)).isEqualTo(expectedCalorie);
        assertThat(totalCalorieOfMealTypeMap.get(MealType.LUNCH)).isEqualTo(expectedCalorie);
        assertThat(totalCalorieOfMealTypeMap.get(MealType.DINNER)).isEqualTo(expectedCalorie);
        assertThat(totalCalorieOfMealTypeMap.get(MealType.BREAKFAST_SNACK))
                .isEqualTo(expectedCalorie);
        assertThat(totalCalorieOfMealTypeMap.get(MealType.LUNCH_SNACK)).isEqualTo(expectedCalorie);
        assertThat(totalCalorieOfMealTypeMap.get(MealType.DINNER_SNACK)).isEqualTo(expectedCalorie);
    }

    private MealLog createMealLog(
            Member member,
            List<MealData> mealData,
            LocalDate date,
            MealLogFixture mealTypeOfFixture) {
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, mealData.size())
                        .mapToObj(idx -> MealImageFixture.DEFAULT.get())
                        .toList();

        if (date != null) {
            return mealTypeOfFixture.getWithDate(date, meals, mealImages, member);
        } else {
            return mealTypeOfFixture.get(meals, mealImages, member);
        }
    }

    private Map<MealType, Integer> toTotalCalorieOfMealTypeMap(
            List<TotalCalorieOfMealType> totalCalorieOfMealTypes) {
        return totalCalorieOfMealTypes.stream()
                .collect(
                        Collectors.toMap(
                                TotalCalorieOfMealType::getMealType,
                                TotalCalorieOfMealType::getTotalCalorie));
    }
}
