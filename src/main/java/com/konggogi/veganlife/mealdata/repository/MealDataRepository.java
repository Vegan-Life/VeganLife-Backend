package com.konggogi.veganlife.mealdata.repository;


import com.konggogi.veganlife.mealdata.domain.MealData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealDataRepository extends JpaRepository<MealData, Long> {

    Page<MealData> findMealDataByNameContaining(String Keyword, Pageable pageable);
}
