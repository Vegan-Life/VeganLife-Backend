package com.konggogi.veganlife.mealdata.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.mealdata.domain.PersonalMealData;
import com.konggogi.veganlife.mealdata.repository.PersonalMealDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalMealDataQueryService {

    private final PersonalMealDataRepository personalMealDataRepository;

    public List<PersonalMealData> searchByKeyword(String keyword, Pageable pageable) {

        return personalMealDataRepository.findPersonalMealDataByNameContaining(keyword, pageable);
    }

    public PersonalMealData search(Long id) {

        return personalMealDataRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.MEAL_DATA_NOT_FOUND));
    }
}
