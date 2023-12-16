package com.konggogi.veganlife.mealdata.repository;


import com.konggogi.veganlife.mealdata.domain.MealData;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealDataRepository extends JpaRepository<MealData, Long> {

    List<MealData> findByNameContaining(String Keyword, Pageable pageable);
}
