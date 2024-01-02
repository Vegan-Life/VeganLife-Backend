package com.konggogi.veganlife.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.IntakeNutrientsFixture;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.fixture.NotificationFixture;
import com.konggogi.veganlife.notification.repository.NotificationRepository;
import com.konggogi.veganlife.notification.service.NotificationService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntakeNotifyServiceTest {
    @Mock NotificationService notificationService;
    @Mock MemberQueryService memberQueryService;
    @Mock NutrientsQueryService nutrientsQueryService;
    @Mock NotificationRepository notificationRepository;
    @InjectMocks IntakeNotifyService intakeNotifyService;

    private final Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("권장 섭취량 대비 금일 섭취 칼로리 30% 초과시 알림")
    void notifyIfOverIntake30PercentageTest() {
        // given
        Long memberId = member.getId();
        IntakeNutrients intakeNutrients =
                IntakeNutrientsFixture.DEFAULT.getOverCalorieOfMember(member, 1.3f);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(nutrientsQueryService.searchDailyIntakeNutrients(memberId, LocalDate.now()))
                .willReturn(intakeNutrients);
        given(
                        notificationRepository.findByDateAndType(
                                anyLong(), any(LocalDate.class), any(NotificationType.class)))
                .willReturn(Optional.empty());
        doNothing()
                .when(notificationService)
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
        // when, then
        assertDoesNotThrow(() -> intakeNotifyService.notifyIfOverIntake(memberId));
        then(notificationRepository)
                .should()
                .findByDateAndType(anyLong(), any(LocalDate.class), any(NotificationType.class));
        then(notificationService)
                .should()
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("권장 섭취량 대비 금일 섭취 칼로리 60% 초과시 알림")
    void notifyIfOverIntake60PercentageTest() {
        // given
        Long memberId = member.getId();
        IntakeNutrients intakeNutrients =
                IntakeNutrientsFixture.DEFAULT.getOverCalorieOfMember(member, 1.6f);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(nutrientsQueryService.searchDailyIntakeNutrients(memberId, LocalDate.now()))
                .willReturn(intakeNutrients);
        given(
                        notificationRepository.findByDateAndType(
                                anyLong(), any(LocalDate.class), any(NotificationType.class)))
                .willReturn(Optional.empty());
        doNothing()
                .when(notificationService)
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
        // when, then
        assertDoesNotThrow(() -> intakeNotifyService.notifyIfOverIntake(memberId));
        then(notificationRepository)
                .should()
                .findByDateAndType(anyLong(), any(LocalDate.class), any(NotificationType.class));
        then(notificationService)
                .should()
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("권장 섭취량 대비 금일 섭취 칼로리 초과시 알림 - 이미 알림을 보낸 경우 보내지 않는다")
    void notifyIfOverIntake60PercentageAlreadySendTest() {
        // given
        Long memberId = member.getId();
        IntakeNutrients intakeNutrients =
                IntakeNutrientsFixture.DEFAULT.getOverCalorieOfMember(member, 1.6f);
        Notification notification = NotificationFixture.INTAKE_OVER_60.get(member);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(nutrientsQueryService.searchDailyIntakeNutrients(memberId, LocalDate.now()))
                .willReturn(intakeNutrients);
        given(
                        notificationRepository.findByDateAndType(
                                anyLong(), any(LocalDate.class), any(NotificationType.class)))
                .willReturn(Optional.of(notification));
        // when, then
        assertDoesNotThrow(() -> intakeNotifyService.notifyIfOverIntake(memberId));
        then(notificationRepository)
                .should()
                .findByDateAndType(anyLong(), any(LocalDate.class), any(NotificationType.class));
        then(notificationService)
                .should(never())
                .sendNotification(any(Member.class), any(NotificationType.class), anyString());
    }

    @Test
    @DisplayName("금일 섭취 칼로리가 초과되지 않으면 알림을 보내지 않는다")
    void notNotifyOverIntakeTest() {
        // given
        Long memberId = member.getId();
        IntakeNutrients intakeNutrients =
                IntakeNutrientsFixture.DEFAULT.getOverCalorieOfMember(member, 1f);
        given(memberQueryService.search(anyLong())).willReturn(member);
        given(nutrientsQueryService.searchDailyIntakeNutrients(memberId, LocalDate.now()))
                .willReturn(intakeNutrients);
        // when, then
        assertDoesNotThrow(() -> intakeNotifyService.notifyIfOverIntake(memberId));
        then(notificationRepository)
                .should(never())
                .findByDateAndType(anyLong(), any(LocalDate.class), any(NotificationType.class));
    }
}
