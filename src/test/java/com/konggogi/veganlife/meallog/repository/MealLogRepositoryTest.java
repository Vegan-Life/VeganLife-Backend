package com.konggogi.veganlife.meallog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.meallog.domain.Meal;
import com.konggogi.veganlife.meallog.domain.MealImage;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.fixture.MealFixture;
import com.konggogi.veganlife.meallog.fixture.MealImageFixture;
import com.konggogi.veganlife.meallog.fixture.MealLogFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing(setDates = false)
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
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();

        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, mealImages, member);
        // when
        MealLog result = mealLogRepository.save(mealLog);
        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getMeals().size()).isEqualTo(mealLog.getMeals().size());
        assertThat(result.getMeals().stream().map(Meal::getId)).allMatch(Objects::nonNull);
        assertThat(result.getMeals().stream().map(Meal::getMealLog)).allMatch(Objects::nonNull);
        assertThat(result.getMealImages().stream().map(MealImage::getId))
                .allMatch(Objects::nonNull);
        assertThat(result.getMealImages().stream().map(MealImage::getMealLog))
                .allMatch(Objects::nonNull);
    }

    @Test
    @DisplayName("회원 Id와 날짜로 MealLog 조회")
    void findAllByMemberIdAndModifiedAtBetweenTest() {
        // given
        Long memberId = member.getId();
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog =
                MealLogFixture.BREAKFAST.getWithDate(LocalDate.now(), meals, mealImages, member);
        MealLog savedMealLog = mealLogRepository.save(mealLog);

        LocalDate date = savedMealLog.getCreatedAt().toLocalDate();
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);
        // when
        List<MealLog> mealLogs =
                mealLogRepository.findAllByMemberIdAndCreatedAtBetween(
                        memberId, startDate, endDate);

        // then
        assertThat(mealLogs).hasSize(1);
        assertThat(mealLogs).extracting(MealLog::getMember).containsOnly(member);
    }

    @Test
    @DisplayName("회원 id와 날짜에 해당하는 MealLog 레코드 조회")
    void findAllByDateTest() {
        // given
        List<Meal> meals1 = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages1 =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog1 =
                MealLogFixture.BREAKFAST.getWithDate(
                        LocalDate.of(2023, 12, 22), meals1, mealImages1, member);
        List<Meal> meals2 = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages2 =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog2 =
                MealLogFixture.BREAKFAST.getWithDate(
                        LocalDate.of(2023, 12, 23), meals2, mealImages2, member);
        mealLogRepository.save(mealLog1);
        mealLogRepository.save(mealLog2);
        // when
        LocalDate date = LocalDate.of(2023, 12, 22);
        List<MealLog> mealLogs = mealLogRepository.findAllByDate(date, member.getId());
        // then
        assertThat(mealLogs.size()).isEqualTo(1);
        assertThat(mealLogs.get(0)).isEqualTo(mealLog1);
    }

    @Test
    @DisplayName("MealLog 업데이트 테스트")
    void mealLogUpdateTest() {
        // given
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, mealImages, member);
        mealLogRepository.saveAndFlush(mealLog);
        // when
        List<Meal> modifiedMeals = new ArrayList<>();
        modifiedMeals.add(MealFixture.DEFAULT.getWithMealLog(mealLog, mealData.get(0))); // 추가
        List<MealImage> modifiedMealImages = new ArrayList<>();
        modifiedMealImages.add(MealImageFixture.DEFAULT.getWithMealLog(mealLog));
        mealLog.updateMeals(modifiedMeals);
        mealLog.updateMealImages(modifiedMealImages);
        mealLogRepository.flush();
        // then
        assertThat(mealLog.getMeals().size()).isEqualTo(1);
        assertThat(mealLog.getMeals().get(0).getMealLog()).isEqualTo(mealLog);
        assertThat(mealLog.getMealImages().size()).isEqualTo(1);
        assertThat(mealLog.getMealImages().get(0).getMealLog()).isEqualTo(mealLog);
    }
}
