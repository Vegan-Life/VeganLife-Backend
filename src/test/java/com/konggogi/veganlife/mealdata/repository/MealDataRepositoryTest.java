package com.konggogi.veganlife.mealdata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class MealDataRepositoryTest {

    @Autowired private MealDataRepository mealDataRepository;
    @Autowired private MemberRepository memberRepository;

    Member member = MemberFixture.DEFAULT_M.getMember();

    @BeforeEach
    void setup() {
        memberRepository.save(member);
    }

    @Test
    @DisplayName("name에 키워드를 포함하는 레코드 조회")
    void findMealDataByNameContainingTest() {
        // given
        List<String> valid = List.of("통밀빵", "통밀크래커");
        List<String> invalid = List.of("가지볶음");
        memberRepository.save(member);
        mealDataRepository.saveAll(
                valid.stream()
                        .map(name -> MealDataFixture.MEAL.getWithName(name, member))
                        .toList());
        mealDataRepository.saveAll(
                invalid.stream()
                        .map(name -> MealDataFixture.MEAL.getWithName(name, member))
                        .toList());
        String keyword = "통";
        Pageable pageable = Pageable.ofSize(12);
        // when
        List<MealData> result = mealDataRepository.findByNameContaining(keyword, pageable);
        // then
        assertThat(result.size()).isEqualTo(valid.size());
        assertThat(result.stream().map(MealData::getName)).allMatch(s -> s.contains(keyword));
    }

    @Test
    @DisplayName("ID에 해당하는 MealData 레코드 조회")
    void findByIdTest() {
        // given
        MealData mealData = MealDataFixture.MEAL.get(member);
        mealDataRepository.save(mealData);
        // when
        Optional<MealData> result1 = mealDataRepository.findById(mealData.getId());
        Optional<MealData> result2 = mealDataRepository.findById(0L);
        // then
        assertThat(result1.isEmpty()).isEqualTo(false);
        assertThat(result2.isEmpty()).isEqualTo(true);
    }
}
