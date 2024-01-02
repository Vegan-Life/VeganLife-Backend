package com.konggogi.veganlife.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.fixture.NotificationFixture;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing(setDates = false)
class NotificationRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired NotificationRepository notificationRepository;

    private final Member member = MemberFixture.DEFAULT_M.get();
    private final LocalDate date = LocalDate.of(2023, 12, 20);
    private final Notification notification = NotificationFixture.SSE.getWithDate(member, date);

    @BeforeEach
    void setup() {
        memberRepository.save(member);
        notificationRepository.save(notification);
    }

    @Test
    @DisplayName("회원 id, 알림 타입, 날짜로 Notification 조회")
    void findByDateAndTypeTest() {
        // given
        Long memberId = member.getId();
        // when
        Optional<Notification> foundNotification =
                notificationRepository.findByDateAndType(memberId, date, NotificationType.SSE);
        // then
        assertThat(foundNotification).isPresent();
    }
}
