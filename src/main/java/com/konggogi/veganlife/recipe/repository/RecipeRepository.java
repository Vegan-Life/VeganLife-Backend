package com.konggogi.veganlife.recipe.repository;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.domain.Recipe;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /** parameter로 들어온 vegeterianType이 null이라면 전체 레코드를 조회한다. */
    @Query(
            "select r from Recipe r where exists"
                    + " (select t from r.recipeTypes t"
                    + " where (t.vegetarianType = :vegetarianType or :vegetarianType is null))")
    Page<Recipe> findAllByRecipeTypes(VegetarianType vegetarianType, Pageable pageable);

    @Query(value = "select * from recipe where recipe.vegetarianType = :vegetarianType order by rand() limit 4", nativeQuery = true)
    List<Recipe> findAllRandom(VegetarianType vegetarianType);
}
