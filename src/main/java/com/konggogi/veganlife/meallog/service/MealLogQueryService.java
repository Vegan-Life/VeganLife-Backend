package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealLogQueryService {

    private final MealLogRepository mealLogRepository;

    public List<MealLog> searchByDateAndMember(LocalDate date, Member member) {

        return mealLogRepository.findAllByDateAndMember(date, member);
    }

    public MealLog search(Long id) {

        return mealLogRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_LOG));
    }

    public List<Tuple> sumCaloriesOfMealTypeByMemberIdAndDate(Long memberId, LocalDate date) {
        return mealLogRepository.sumCaloriesOfMealTypeByMemberIdAndCreatedAt(memberId, date);
    }

    public List<Tuple> sumCaloriesOfMealTypeByMemberIdAndDateBetween(
            Long memberId, LocalDate startDate, LocalDate endDate) {
        return mealLogRepository.sumCaloriesOfMealTypeByMemberIdAndCreatedAtBetween(
                memberId, startDate, endDate);
    }
}
