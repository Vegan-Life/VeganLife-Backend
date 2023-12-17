package com.konggogi.veganlife.meallog.repository;


import com.konggogi.veganlife.meallog.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {}
