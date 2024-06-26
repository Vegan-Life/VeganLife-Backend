package com.konggogi.veganlife.mealdata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.global.exception.EntityAccessDeniedException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.controller.dto.request.MealDataUpdateRequest;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapper;
import com.konggogi.veganlife.mealdata.domain.mapper.MealDataMapperImpl;
import com.konggogi.veganlife.mealdata.fixture.MealDataFixture;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MealDataServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock MealDataRepository mealDataRepository;
    @Spy MealDataMapper mealDataMapper = new MealDataMapperImpl();
    @InjectMocks MealDataService mealDataService;

    private Member member;

    @BeforeEach
    void init() {
        member = MemberFixture.DEFAULT_M.get();
    }

    @Test
    @DisplayName("식품 데이터를 등록")
    void addTest() {

        MealDataUpdateRequest request =
                new MealDataUpdateRequest("통밀빵", 300, 100, 210, 30, 5, 3, "g");

        given(memberQueryService.search(anyLong())).willReturn(member);

        mealDataService.add(request, 1L);
        MealData mealData = mealDataMapper.toEntity(request, member);

        then(memberQueryService).should(times(1)).search(anyLong());
        then(mealDataRepository).should(times(1)).save(any(MealData.class));
        assertThat(mealData.getCaloriePerUnit()).isEqualTo((double) 210 / 100);
        assertThat(mealData.getCarbsPerUnit()).isEqualTo((double) 30 / 100);
        assertThat(mealData.getProteinPerUnit()).isEqualTo((double) 5 / 100);
        assertThat(mealData.getFatPerUnit()).isEqualTo((double) 3 / 100);
    }

    @Test
    @DisplayName("회원 Id로 MealData 모두 삭제")
    void removeAllTest() {
        Long memberId = 1L;
        member = spy(member);
        given(member.getId()).willReturn(memberId);
        // when, then
        assertDoesNotThrow(() -> mealDataService.removeAll(member.getId()));
        then(mealDataRepository).should().deleteAllByMemberId(anyLong());
    }

    @Test
    @DisplayName("해당하는 id의 MealData를 삭제한다.")
    void removeById() {

        Long memberId = 1L;
        member = spy(member);
        given(member.getId()).willReturn(memberId);
        Long mealDataId = 2L;
        MealData mealData = MealDataFixture.TOTAL_AMOUNT.get(member);

        given(mealDataRepository.findById(eq(mealDataId))).willReturn(Optional.of(mealData));

        assertThatCode(() -> mealDataService.removeById(mealDataId, memberId))
                .doesNotThrowAnyException();
        then(mealDataRepository).should(times(1)).delete(eq(mealData));
    }

    @Test
    @DisplayName("id에 해당하는 MealData가 존재하지 않을 경우 NotFoundEntityException이 발생한다.")
    void throwNotFoundEntityExceptionIfMealDataNotExistsWhenRemoveMealData() {

        Long memberId = 1L;
        Long mealDataId = 2L;
        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA))
                .given(mealDataRepository)
                .findById(anyLong());

        assertThatCode(() -> mealDataService.removeById(mealDataId, memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEAL_DATA.getDescription());
    }

    @Test
    @DisplayName("삭제하고자 하는 MealData가 로그인한 사용자의 소유가 아닐 경우 EntityAccessDeniedException이 발생한다.")
    void throwEntityAccessDeniedExceptionIfMealDataNotOwnedByMemberWhenRemoveMealData() {

        Long memberId = 1L;
        member = spy(member);
        given(member.getId()).willReturn(memberId);
        Long mealDataId = 2L;
        MealData mealData = spy(MealDataFixture.TOTAL_AMOUNT.get(member));
        given(mealData.isOwnedBy(eq(memberId))).willReturn(false);

        given(mealDataRepository.findById(eq(mealDataId))).willReturn(Optional.of(mealData));

        assertThatCode(() -> mealDataService.removeById(mealDataId, memberId))
                .isInstanceOf(EntityAccessDeniedException.class)
                .hasMessage(ErrorCode.MEAL_DATA_ACCESS_DENIED.getDescription());
    }

    @Test
    @DisplayName("해당하는 id의 MealData를 수정한다.")
    void modifyById() {
        Long memberId = 1L;
        member = spy(member);
        given(member.getId()).willReturn(memberId);
        Long mealDataId = 2L;
        MealData mealData = MealDataFixture.TOTAL_AMOUNT.get(member);

        MealDataUpdateRequest request =
                new MealDataUpdateRequest("통밀빵", 300, 100, 210, 30, 5, 3, "그램");

        given(mealDataRepository.findById(eq(mealDataId))).willReturn(Optional.of(mealData));

        assertThatCode(() -> mealDataService.modifyById(request, mealDataId, memberId))
                .doesNotThrowAnyException();
        assertAll(
                () -> {
                    assertThat(mealData.getName()).isEqualTo(request.name());
                    assertThat(mealData.getAmount()).isEqualTo(request.amount());
                    assertThat(mealData.getAmountPerServe()).isEqualTo(request.amountPerServe());
                    assertThat(mealData.getCaloriePerUnit())
                            .isEqualTo(
                                    (double) request.caloriePerServe() / request.amountPerServe());
                    assertThat(mealData.getFatPerUnit())
                            .isEqualTo((double) request.fatPerServe() / request.amountPerServe());
                    assertThat(mealData.getProteinPerUnit())
                            .isEqualTo(
                                    (double) request.proteinPerServe() / request.amountPerServe());
                    assertThat(mealData.getCarbsPerUnit())
                            .isEqualTo((double) request.carbsPerServe() / request.amountPerServe());
                    assertThat(mealData.getIntakeUnit()).isEqualTo(request.intakeUnit());
                });
    }

    @Test
    @DisplayName("id에 해당하는 MealData가 존재하지 않을 경우 NotFoundEntityException이 발생한다.")
    void throwNotFoundEntityExceptionIfMealDataNotExistsWhenModifyMealData() {

        Long memberId = 1L;
        Long mealDataId = 2L;
        MealDataUpdateRequest request =
                new MealDataUpdateRequest("통밀빵", 300, 100, 210, 30, 5, 3, "그램");
        willThrow(new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA))
                .given(mealDataRepository)
                .findById(anyLong());

        assertThatCode(() -> mealDataService.modifyById(request, mealDataId, memberId))
                .isInstanceOf(NotFoundEntityException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEAL_DATA.getDescription());
    }

    @Test
    @DisplayName("삭제하고자 하는 MealData가 로그인한 사용자의 소유가 아닐 경우 EntityAccessDeniedException이 발생한다.")
    void throwEntityAccessDeniedExceptionIfMealDataNotOwnedByMemberWhenModifyMealData() {

        Long memberId = 1L;
        member = spy(member);
        given(member.getId()).willReturn(memberId);
        Long mealDataId = 2L;
        MealData mealData = spy(MealDataFixture.TOTAL_AMOUNT.get(member));
        given(mealData.isOwnedBy(eq(memberId))).willReturn(false);
        MealDataUpdateRequest request =
                new MealDataUpdateRequest("통밀빵", 300, 100, 210, 30, 5, 3, "그램");

        given(mealDataRepository.findById(eq(mealDataId))).willReturn(Optional.of(mealData));

        assertThatCode(() -> mealDataService.modifyById(request, mealDataId, memberId))
                .isInstanceOf(EntityAccessDeniedException.class)
                .hasMessage(ErrorCode.MEAL_DATA_ACCESS_DENIED.getDescription());
    }
}
