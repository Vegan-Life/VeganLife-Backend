package com.konggogi.veganlife.meallog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MealLogRepositoryTest {

    @Autowired MealLogRepository mealLogRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MealDataRepository mealDataRepository;

    Member member = Member.builder().email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.MEAL.get(member),
                    MealDataFixture.MEAL.get(member),
                    MealDataFixture.MEAL.get(member));

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        mealDataRepository.saveAll(mealData);
    }

    @Test
    @DisplayName("MealLog 저장 테스트")
    void mealLogSaveTest() {
        // given
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, member);
        // when
        MealLog result = mealLogRepository.save(mealLog);
        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getMeals().size()).isEqualTo(mealLog.getMeals().size());
        assertThat(result.getMeals().stream().map(Meal::getId)).allMatch(Objects::nonNull);
        assertThat(result.getMeals().stream().map(Meal::getMealLog)).allMatch(Objects::nonNull);
    }

    @Test
    @DisplayName("회원 Id와 날짜로 MealLog 조회")
    void findAllByMemberIdAndModifiedAtBetweenTest() {
        // given
        Long memberId = member.getId();
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, member);
        MealLog savedMealLog = mealLogRepository.save(mealLog);

        LocalDate date = savedMealLog.getModifiedAt().toLocalDate();
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);
        // when
        List<MealLog> mealLogs =
                mealLogRepository.findAllByMemberIdAndModifiedAtBetween(
                        memberId, startDate, endDate);
        // then
        assertThat(mealLogs).hasSize(1);
        assertThat(mealLogs).extracting(MealLog::getMember).containsOnly(member);
    }
}
