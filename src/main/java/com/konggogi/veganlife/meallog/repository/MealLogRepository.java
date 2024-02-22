package com.konggogi.veganlife.meallog.repository;


import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    @Query(
            "select ml.mealType as mealType, SUM(m.calorie) as totalCalories "
                    + "from MealLog ml join ml.meals m "
                    + "where ml.member.id = :memberId "
                    + "and cast(ml.createdAt as localdate) = :date "
                    + "group by ml.mealType")
    List<Tuple> sumCaloriesOfMealTypeByMemberIdAndCreatedAt(Long memberId, LocalDate date);

    @Query(
            "select ml.mealType as mealType, SUM(m.calorie) as totalCalories "
                    + "from MealLog ml join ml.meals m "
                    + "where ml.member.id = :memberId "
                    + "and cast(ml.createdAt as localdate) between :startDate and :endDate "
                    + "group by ml.mealType")
    List<Tuple> sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetween(
            Long memberId, LocalDate startDate, LocalDate endDate);

    @Query(" select distinct m from MealLog m join fetch m.meals" + " where m.id = :id")
    Optional<MealLog> findById(Long id);

    @Query(
            " select distinct m from MealLog m join fetch m.meals"
                    + " where cast(m.createdAt as localdate) = :date and m.member = :member")
    List<MealLog> findAllByDateAndMember(LocalDate date, Member member);

    void deleteAllByMemberId(Long memberId);
}
