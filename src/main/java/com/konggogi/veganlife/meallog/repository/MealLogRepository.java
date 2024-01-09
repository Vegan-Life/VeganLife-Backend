package com.konggogi.veganlife.meallog.repository;


import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findAllByMemberIdAndCreatedAtBetween(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(" select distinct m from MealLog m join fetch m.meals" + " where m.id = :id")
    Optional<MealLog> findById(Long id);

    @Query(
            " select distinct m from MealLog m join fetch m.meals"
                    + " where cast(m.createdAt as localdate) = :date and m.member = :member")
    List<MealLog> findAllByDateAndMember(LocalDate date, Member member);

    void deleteAllByMemberId(Long memberId);
}
