package com.konggogi.veganlife.recipe.repository;


import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // TODO: in vs exists 성능 비교
    /** parameter로 들어온 vegeterianType이 null이라면 전체 레코드를 조회한다. */
    @Query(
            "select r from Recipe r"
                    + " where exists (select t from r.recipeTypes t"
                    + " where (t.vegetarianType = :vegetarianType or :vegetarianType is null))")
    Page<Recipe> findAllByRecipeTypes(VegetarianType vegetarianType, Pageable pageable);

    @Query(
            " select count(r) from Recipe r"
                    + " where exists (select t from r.recipeTypes t where t.vegetarianType = :vegetarianType)")
    int countByRecipeType(VegetarianType vegetarianType);

    /** keyword는 레시피 제목 혹은 작성자 닉네임이 될 수 있다. */
    @Query(
            " select r from Recipe r"
                    + " where r.name like %:keyword%"
                    + " or r.member.nickname like %:keyword%")
    Page<Recipe> findAllByKeyword(String keyword, Pageable pageable);
}
