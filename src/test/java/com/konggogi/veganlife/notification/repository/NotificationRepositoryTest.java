package com.konggogi.veganlife.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.notification.domain.Notification;
import com.konggogi.veganlife.notification.domain.NotificationType;
import com.konggogi.veganlife.notification.fixture.NotificationFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing(setDates = false)
class NotificationRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired NotificationRepository notificationRepository;

    private final Member member = MemberFixture.DEFAULT_M.get();
    private final LocalDateTime date = LocalDateTime.of(2023, 12, 20, 0, 0, 0);
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
                notificationRepository.findByDateAndType(
                        memberId, date.toLocalDate(), NotificationType.SSE);
        // then
        assertThat(foundNotification).isPresent();
    }

    @Test
    @DisplayName("회원의 Notification 모두 삭제")
    void deleteAllByMemberIdTest() {
        // given
        Member otherMember = MemberFixture.DEFAULT_F.get();
        memberRepository.save(otherMember);
        Notification otherNotification = NotificationFixture.SSE.get(otherMember);
        notificationRepository.save(otherNotification);
        // when
        notificationRepository.deleteAllByMemberId(member.getId());
        // then
        assertThat(notificationRepository.findById(otherNotification.getId())).isPresent();
        assertThat(notificationRepository.findById(notification.getId())).isEmpty();
    }

    @Test
    @DisplayName("사용자의 알림 목록 조회 시 SSE 연결 설정 알림은 제외하고 조회한다.")
    void findByMember_ExceptSSENotification() {
        // given
        List<Notification> notifications =
                List.of(
                        NotificationFixture.SSE.getWithDate(
                                member, LocalDateTime.of(2024, 10, 15, 0, 0, 0)),
                        NotificationFixture.COMMENT.getWithDate(
                                member, LocalDateTime.of(2024, 10, 15, 1, 0, 0)),
                        NotificationFixture.INTAKE_OVER_30.getWithDate(
                                member, LocalDateTime.of(2024, 10, 15, 2, 0, 0)));
        notificationRepository.saveAll(notifications);

        // when
        List<Notification> found =
                notificationRepository
                        .findAllByMember(member.getId(), Pageable.ofSize(10))
                        .getContent();
        // then
        assertAll(
                () -> {
                    assertThat(found).hasSize(2);
                    found.forEach(
                            (result) ->
                                    assertThat(result.getType())
                                            .isNotEqualTo(NotificationType.SSE));
                });
    }

    @Test
    @DisplayName("사용자의 알림 목록은 생성날짜의 역순으로 조회된다.")
    void findByMemberTest_CreatedAtIsDesc() {
        // given
        List<Notification> notifications =
                List.of(
                        NotificationFixture.COMMENT.getWithDate(
                                member, LocalDateTime.of(2024, 10, 15, 1, 0, 0)),
                        NotificationFixture.INTAKE_OVER_30.getWithDate(
                                member, LocalDateTime.of(2024, 10, 14, 2, 0, 0)),
                        NotificationFixture.MENTION.getWithDate(
                                member, LocalDateTime.of(2024, 10, 16, 3, 0, 0)));
        notificationRepository.saveAll(notifications);

        // when
        List<Notification> found =
                notificationRepository
                        .findAllByMember(member.getId(), Pageable.ofSize(10))
                        .getContent();
        // then
        assertAll(
                () -> {
                    assertThat(found).hasSize(3);
                    assertThat(found.get(0).getType()).isEqualTo(NotificationType.MENTION);
                    assertThat(found.get(1).getType()).isEqualTo(NotificationType.COMMENT);
                    assertThat(found.get(2).getType()).isEqualTo(NotificationType.INTAKE_OVER_30);
                });
    }
}
