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
import com.konggogi.veganlife.meallog.service.dto.MealLogDetails;
import com.konggogi.veganlife.meallog.service.dto.MealLogList;
import com.konggogi.veganlife.member.domain.Member;
import java.time.LocalDate;
import java.util.ArrayList;
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
                    MealDataFixture.MEAL.get(1L, member),
                    MealDataFixture.MEAL.get(2L, member),
                    MealDataFixture.MEAL.get(3L, member));

    @Test
    @DisplayName("해당 날짜의 MealLog 목록 조회 - 이미지가 있는 경우")
    void searchByDateTest() {

        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        List<MealImage> mealImages =
                IntStream.range(0, 3).mapToObj(idx -> MealImageFixture.DEFAULT.get()).toList();
        LocalDate date = LocalDate.of(2023, 12, 22);
        List<MealLog> mealLogs =
                List.of(MealLogFixture.BREAKFAST.getWithDate(date, meals, mealImages, member));
        String expectedThumbnailUrl = mealLogs.get(0).getMealImages().get(0).getImageUrl();
        int expectedTotalCalorie =
                mealLogs.get(0).getMeals().stream().mapToInt(Meal::getCalorie).sum();
        given(mealLogRepository.findAllByDate(date, member.getId())).willReturn(mealLogs);

        List<MealLogList> mealLogList = mealLogQueryService.searchByDate(date, member.getId());

        assertThat(mealLogList.size()).isEqualTo(1);
        assertThat(mealLogList.get(0).mealLog()).isEqualTo(mealLogs.get(0));
        assertThat(mealLogList.get(0).thumbnailUrl()).isEqualTo(expectedThumbnailUrl);
        assertThat(mealLogList.get(0).totalCalorie()).isEqualTo(expectedTotalCalorie);
    }

    @Test
    @DisplayName("해당 날짜의 MealLog 목록 조회 - 이미지가 없는 경우")
    void searchByDateWithoutImageTest() {

        List<Meal> meals = mealData.stream().map(MealFixture.DEFAULT::get).toList();
        LocalDate date = LocalDate.of(2023, 12, 22);
        List<MealLog> mealLogs =
                List.of(
                        MealLogFixture.BREAKFAST.getWithDate(
                                date, meals, new ArrayList<>(), member));
        int expectedTotalCalorie =
                mealLogs.get(0).getMeals().stream().mapToInt(Meal::getCalorie).sum();
        given(mealLogRepository.findAllByDate(date, member.getId())).willReturn(mealLogs);

        List<MealLogList> mealLogList = mealLogQueryService.searchByDate(date, member.getId());

        assertThat(mealLogList.size()).isEqualTo(1);
        assertThat(mealLogList.get(0).mealLog()).isEqualTo(mealLogs.get(0));
        assertThat(mealLogList.get(0).thumbnailUrl()).isEqualTo(null);
        assertThat(mealLogList.get(0).totalCalorie()).isEqualTo(expectedTotalCalorie);
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

        MealLogDetails mealLogDetails = mealLogQueryService.search(1L);

        assertThat(mealLogDetails.mealLog()).isEqualTo(mealLog);
        assertThat(mealLogDetails.meals().size()).isEqualTo(3);
        assertThat(mealLogDetails.mealImages().size()).isEqualTo(3);
        assertThat(mealLogDetails.intakeNutrients().calorie()).isEqualTo(300);
        assertThat(mealLogDetails.intakeNutrients().carbs()).isEqualTo(30);
        assertThat(mealLogDetails.intakeNutrients().protein()).isEqualTo(30);
        assertThat(mealLogDetails.intakeNutrients().fat()).isEqualTo(30);
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
