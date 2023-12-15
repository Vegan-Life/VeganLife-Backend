package com.konggogi.veganlife.mealdata.repository;


import com.konggogi.veganlife.mealdata.domain.PersonalMealData;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalMealDataRepository extends JpaRepository<PersonalMealData, Long> {

    List<PersonalMealData> findPersonalMealDataByNameContaining(String Keyword, Pageable pageable);
}
