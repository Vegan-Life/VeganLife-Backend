package com.konggogi.veganlife.mealdata.repository;


import com.konggogi.veganlife.mealdata.domain.MealData;
import com.konggogi.veganlife.mealdata.domain.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealDataRepository extends JpaRepository<MealData, Long> {

    Page<MealData> findByNameContainingAndOwnerType(
            String keyword, OwnerType ownerType, Pageable pageable);
}
