package com.konggogi.veganlife.meallog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

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
import com.konggogi.veganlife.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MealLogQueryServiceTest {

    @Mock MealLogRepository mealLogRepository;
    @InjectMocks MealLogQueryService mealLogQueryService;

    Member member = Member.builder().email("test123@test.com").build();
    List<MealData> mealData =
            List.of(
                    MealDataFixture.TOTAL_AMOUNT.get(1L, member),
                    MealDataFixture.TOTAL_AMOUNT.get(2L, member),
                    MealDataFixture.TOTAL_AMOUNT.get(3L, member));

    @Test
    @DisplayName("해당 날짜의 MealLog 목록 조회")
    void searchByDateTest() {

        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        LocalDate date = LocalDate.of(2023, 12, 22);
        List<MealLog> mealLogs =
                List.of(MealLogFixture.BREAKFAST.getWithDate(date, meals, mealImages, member));
        given(mealLogRepository.findAllByDateAndMember(date, member)).willReturn(mealLogs);

        List<MealLog> found = mealLogQueryService.searchByDateAndMember(date, member);

        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0)).isEqualTo(mealLogs.get(0));
    }

    @Test
    @DisplayName("해당 id의 MealLog 상세 조회")
    void searchTest() {

        List<Meal> meals =
                List.of(
                        MealFixture.DEFAULT.get(1L, mealData.get(0)),
                        MealFixture.DEFAULT.get(2L, mealData.get(1)),
                        MealFixture.DEFAULT.get(3L, mealData.get(2)));
        List<MealImage> mealImages =
                LongStream.range(1, 4).mapToObj(MealImageFixture.DEFAULT::get).toList();
        MealLog mealLog = MealLogFixture.BREAKFAST.get(1L, meals, mealImages, member);

        given(mealLogRepository.findById(1L)).willReturn(Optional.ofNullable(mealLog));

        MealLog found = mealLogQueryService.search(1L);

        assertThat(found).isEqualTo(mealLog);
        assertThat(found.getMeals().size()).isEqualTo(3);
        assertThat(found.getMealImages().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("해당 id의 MealLog 상세 조회 실패 - Not Found")
    void searchNotFoundExceptionTest() {

        given(mealLogRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> mealLogQueryService.search(1L))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEAL_LOG.getDescription());
    }
}
