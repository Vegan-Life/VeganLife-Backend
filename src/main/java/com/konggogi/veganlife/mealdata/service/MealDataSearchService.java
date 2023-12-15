package com.konggogi.veganlife.mealdata.service;


import com.konggogi.veganlife.mealdata.domain.AccessType;
import com.konggogi.veganlife.mealdata.service.dto.MealDataDetailsDto;
import com.konggogi.veganlife.mealdata.service.dto.MealDataListDto;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealDataSearchService {

    private final MealDataQueryService mealDataQueryService;
    private final PersonalMealDataQueryService personalMealDataQueryService;

    public List<MealDataListDto> searchByKeyword(String keyword, Pageable pageable) {

        return Stream.concat(
                        mealDataQueryService.searchByKeyword(keyword, pageable).stream()
                                .map(MealDataListDto::fromMealData),
                        personalMealDataQueryService.searchByKeyword(keyword, pageable).stream()
                                .map(MealDataListDto::fromPersonalMealData))
                .sorted(Comparator.comparing((MealDataListDto::name)))
                .toList();
    }

    public MealDataDetailsDto search(Long id, AccessType accessType) {

        if (accessType.equals(AccessType.ALL)) {
            return MealDataDetailsDto.fromMealData(mealDataQueryService.search(id));
        }
        return MealDataDetailsDto.fromPersonalMealData(personalMealDataQueryService.search(id));
    }
}
