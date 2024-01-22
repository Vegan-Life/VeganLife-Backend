package com.konggogi.veganlife.recipe.repository;


import com.konggogi.veganlife.recipe.domain.RecipeLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    Optional<RecipeLike> findByRecipeIdAndMemberId(Long recipeId, Long memberId);
}
