package com.konggogi.veganlife.member.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.dto.IntakeNutrients;
import com.konggogi.veganlife.sse.domain.NotificationMessage;
import com.konggogi.veganlife.sse.domain.NotificationType;
import com.konggogi.veganlife.sse.repository.NotificationRepository;
import com.konggogi.veganlife.sse.service.NotificationService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntakeNotifyService {
    private final NotificationService notificationService;
    private final MemberQueryService memberQueryService;
    private final NutrientsQueryService nutrientsQueryService;
    private final NotificationRepository notificationRepository;

    public void notifyIfOverIntake(Long memberId) {
        Member member = memberQueryService.search(memberId);
        IntakeNutrients intakeNutrients =
                nutrientsQueryService.searchDailyIntakeNutrients(memberId, LocalDate.now());
        int dailyAMR = member.getAMR();
        int todayIntakeCalorie = intakeNutrients.calorie();
        int overCalorie = todayIntakeCalorie - dailyAMR;

        getNotificationType(dailyAMR, todayIntakeCalorie)
                .ifPresent(type -> sendOverIntakeNotificationIfNotSend(member, type, overCalorie));
    }

    private Optional<NotificationType> getNotificationType(int dailyAMR, int todayIntakeCalorie) {
        int over30PercentageOfAMR = (int) (dailyAMR * 1.3);
        int over60PercentageOfAMR = (int) (dailyAMR * 1.6);

        if (todayIntakeCalorie >= over60PercentageOfAMR) {
            return Optional.of(NotificationType.INTAKE_OVER_60);
        } else if (todayIntakeCalorie >= over30PercentageOfAMR) {
            return Optional.of(NotificationType.INTAKE_OVER_30);
        }
        return Optional.empty();
    }

    private void sendOverIntakeNotificationIfNotSend(
            Member member, NotificationType type, int overCalorie) {
        notificationRepository
                .findByDateAndType(member.getId(), LocalDate.now(), type)
                .ifPresentOrElse(
                        notification -> {},
                        () ->
                                notificationService.sendNotification(
                                        member,
                                        type,
                                        NotificationMessage.OVER_INTAKE.getMessage(overCalorie)));
    }
}
