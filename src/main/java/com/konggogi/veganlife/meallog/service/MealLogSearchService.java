package com.konggogi.veganlife.meallog.service;


import com.konggogi.veganlife.meallog.domain.mapper.MealLogMapper;
import com.konggogi.veganlife.meallog.service.dto.MealLogDetailsDto;
import com.konggogi.veganlife.meallog.service.dto.MealLogListDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealLogSearchService {

    private final MealLogQueryService mealLogQueryService;
    private final MealLogMapper mealLogMapper;

    public List<MealLogListDto> searchByDate(LocalDate date, Long memberId) {

        return mealLogQueryService.searchByDate(date, memberId).stream()
                .map(mealLogMapper::toMealLogListDto)
                .toList();
    }

    public MealLogDetailsDto searchById(Long id) {

        return mealLogMapper.toMealDetailsDto(mealLogQueryService.searchById(id));
    }
}
