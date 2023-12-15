package com.konggogi.veganlife.mealdata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class MealDataRepositoryTest {

    @Autowired private MealDataRepository mealDataRepository;

    @Test
    @DisplayName("name에 키워드를 포함하는 레코드 조회")
    void findMealDataByNameContainingTest() {
        // given
        List<String> valid = List.of("통밀빵", "통밀크래커");
        List<String> invalid = List.of("가지볶음");
        mealDataRepository.saveAll(valid.stream().map(MealDataFixture.MEAL::getWithName).toList());
        mealDataRepository.saveAll(
                invalid.stream().map(MealDataFixture.MEAL::getWithName).toList());
        String keyword = "통";
        Pageable pageable = Pageable.ofSize(12);
        // when
        Page<MealData> result = mealDataRepository.findMealDataByNameContaining(keyword, pageable);
        // then
        assertThat(result.getNumberOfElements()).isEqualTo(valid.size());
        assertThat(result.getContent().stream().map(MealData::getName))
                .allMatch(s -> s.contains(keyword));
    }

    @Test
    @DisplayName("ID에 해당하는 MealData 레코드 조회")
    void findByIdTest() {
        // given
        MealData mealData = MealDataFixture.MEAL.get();
        mealDataRepository.save(mealData);
        // when
        Optional<MealData> result1 = mealDataRepository.findById(mealData.getId());
        Optional<MealData> result2 = mealDataRepository.findById(-999L);
        // then
        assertThat(result1.isEmpty()).isEqualTo(false);
        assertThat(result2.isEmpty()).isEqualTo(true);
    }
}
