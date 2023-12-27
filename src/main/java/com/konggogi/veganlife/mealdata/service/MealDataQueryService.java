package com.konggogi.veganlife.mealdata.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import com.konggogi.veganlife.mealdata.repository.MealDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MealDataQueryService {

    private final MealDataRepository mealDataRepository;

    public Page<MealData> searchByKeyword(String keyword, OwnerType type, Pageable pageable) {

        return mealDataRepository.findByNameContainingAndOwnerType(keyword, type, pageable);
    }

    public MealData search(Long id) {

        return mealDataRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_MEAL_DATA));
    }
}
