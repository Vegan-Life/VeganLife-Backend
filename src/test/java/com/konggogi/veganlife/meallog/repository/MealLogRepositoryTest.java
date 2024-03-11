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
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    @DisplayName("MealLog 저장 테스트")
    void mealLogSaveTest() {
        // given
        MealLog mealLog = createMealLog(member, mealData, null, MealLogFixture.BREAKFAST);
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
        List<MealLog> mealLogs = mealLogRepository.findAllByDateAndMember(date, member);
        // then
        assertThat(mealLogs).hasSize(1);
        assertThat(mealLogs.get(0)).isEqualTo(mealLog1);
    }

    @Test
    @DisplayName("MealLog 수정 테스트 - 연관된 자식 엔티티를 삭제하고 새로 삽입한다")
    void mealLogUpdateTest() {
        // given
        MealLog mealLog = createMealLog(member, mealData, null, MealLogFixture.BREAKFAST);
        mealLogRepository.saveAndFlush(mealLog);
        // when
        List<Meal> modifiedMeals = new ArrayList<>();
        modifiedMeals.add(MealFixture.DEFAULT.getWithMealLog(mealLog, mealData.get(0))); // 추가
        List<MealImage> modifiedMealImages = new ArrayList<>();
        modifiedMealImages.add(MealImageFixture.DEFAULT.getWithMealLog(mealLog));
        mealLog.modifyMeals(modifiedMeals);
        mealLog.modifyMealImages(modifiedMealImages);
        mealLogRepository.flush();
        // then
        assertThat(mealLog.getMeals()).hasSize(1);
        assertThat(mealLog.getMeals().get(0).getMealLog()).isEqualTo(mealLog);
        assertThat(mealLog.getMealImages()).hasSize(1);
        assertThat(mealLog.getMealImages().get(0).getMealLog()).isEqualTo(mealLog);
    }

    @Test
    @DisplayName("MealLog 삭제 테스트 - 연관된 자식 엔티티도 같이 삭제된다")
    void mealLogDeleteTest() {
        // given
        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(meals, mealImages, member);
        mealLogRepository.saveAndFlush(mealLog);
        // when
        mealLogRepository.deleteById(mealLog.getId());
        mealLogRepository.flush();
        // then
        Optional<MealLog> found = mealLogRepository.findById(mealLog.getId());
        List<Optional<Meal>> foundMeals =
                meals.stream().map(m -> mealRepository.findById(m.getId())).toList();
        List<Optional<MealImage>> foundMealImages =
                mealImages.stream().map(m -> mealImageRepository.findById(m.getId())).toList();
        assertThat(found).isEmpty();
        assertThat(foundMeals.stream().allMatch(Optional::isEmpty)).isTrue();
        assertThat(foundMealImages.stream().allMatch(Optional::isEmpty)).isTrue();
    }

    @Test
    @DisplayName("회원의 MealLog 모두 삭제")
    void deleteAllByMemberIdTest() {
        // given
        MealLog mealLog = createMealLog(member, mealData, null, MealLogFixture.BREAKFAST);
        Member otherMember = MemberFixture.DEFAULT_F.get();
        memberRepository.save(otherMember);
        MealLog otherMealLog = createMealLog(otherMember, mealData, null, MealLogFixture.BREAKFAST);
        mealLogRepository.save(mealLog);
        mealLogRepository.save(otherMealLog);
        // when
        mealLogRepository.deleteAllByMemberId(member.getId());
        // then
        assertThat(mealLogRepository.findById(otherMealLog.getId())).isPresent();
        assertThat(mealLogRepository.findById(mealLog.getId())).isEmpty();
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
}
