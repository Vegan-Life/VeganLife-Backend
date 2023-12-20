package com.konggogi.veganlife.meallog.repository;


import com.konggogi.veganlife.meallog.domain.MealLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findAllByMemberIdAndModifiedAtBetween(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate);
}
