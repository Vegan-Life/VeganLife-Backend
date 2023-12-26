package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.meallog.domain.MealLog;
import com.konggogi.veganlife.meallog.repository.MealLogRepository;
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

    public List<MealLog> searchByDate(LocalDate date, Long memberId) {

        return mealLogRepository.findAllByDate(date, memberId);
    }

    public MealLog searchById(Long id) {

        return mealLogRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_LOG));
    }
}
