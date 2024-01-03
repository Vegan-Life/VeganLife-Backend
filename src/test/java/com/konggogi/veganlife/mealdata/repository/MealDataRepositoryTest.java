package com.konggogi.veganlife.mealdata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class MealDataRepositoryTest {

    @Autowired private MealDataRepository mealDataRepository;
    @Autowired private MemberRepository memberRepository;

    private final Member member = MemberFixture.DEFAULT_M.get();

    @BeforeEach
    void setup() {
        memberRepository.save(member);
    }

    @Test
    @DisplayName("name에 키워드를 포함하는 레코드 조회")
    void findMealDataByNameContainingTest() {
        // given
        List<MealData> valid =
                List.of(
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "통밀빵", OwnerType.ALL, member),
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "통밀크래커", OwnerType.ALL, member));
        List<MealData> invalid =
                List.of(
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "가지볶음", OwnerType.ALL, member),
                        MealDataFixture.TOTAL_AMOUNT.getWithNameAndOwnerType(
                                "통크", OwnerType.MEMBER, member));
        mealDataRepository.saveAll(valid);
        mealDataRepository.saveAll(invalid);
        // when
        Page<MealData> result =
                mealDataRepository.findByNameContainingAndOwnerType(
                        "통", OwnerType.ALL, Pageable.ofSize(20));
        // then
        assertThat(result.getNumberOfElements()).isEqualTo(valid.size());
        assertThat(result.getTotalElements()).isEqualTo(valid.size());
        assertThat(result.map(MealData::getName)).allMatch(name -> name.contains("통"));
        assertThat(result.map(MealData::getOwnerType))
                .allMatch(ownerType -> ownerType.equals(OwnerType.ALL));
    }

    @Test
    @DisplayName("ID에 해당하는 MealData 레코드 조회")
    void findByIdTest() {
        // given
        MealData mealData = MealDataFixture.TOTAL_AMOUNT.get(member);
        mealDataRepository.save(mealData);
        // when
        Optional<MealData> result1 = mealDataRepository.findById(mealData.getId());
        Optional<MealData> result2 = mealDataRepository.findById(0L);
        // then
        assertThat(result1.isEmpty()).isEqualTo(false);
        assertThat(result2.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("MealData 저장")
    void saveTest() {
        // given
        MealData mealData = MealDataFixture.TOTAL_AMOUNT.get(member);
        // when
        mealDataRepository.save(mealData);
        // then
        assertThat(mealData.getId()).matches(Objects::nonNull);
    }

    @Test
    @DisplayName("회원의 MealData 모두 삭제")
    void deleteAllByMemberIdTest() {
        // given
        Member otherMember = MemberFixture.DEFAULT_F.get();
        memberRepository.save(otherMember);
        MealData mealData = MealDataFixture.TOTAL_AMOUNT.get(member);
        MealData otherMealData = MealDataFixture.TOTAL_AMOUNT.get(otherMember);
        mealDataRepository.save(mealData);
        mealDataRepository.save(otherMealData);
        // when
        mealDataRepository.deleteAllByMemberId(member.getId());
        // then
        assertThat(mealDataRepository.findById(otherMealData.getId())).isPresent();
        assertThat(mealDataRepository.findById(mealData.getId())).isEmpty();
    }
}
