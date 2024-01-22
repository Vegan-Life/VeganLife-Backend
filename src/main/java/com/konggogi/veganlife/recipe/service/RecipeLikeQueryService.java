package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.recipe.domain.RecipeLike;
import com.konggogi.veganlife.recipe.repository.RecipeLikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeLikeQueryService {

    private final RecipeLikeRepository recipeLikeRepository;

    public Optional<RecipeLike> searchByRecipeIdAndMemberId(Long recipeId, Long memberId) {

        return recipeLikeRepository.findByRecipeIdAndMemberId(recipeId, memberId);
    }
}
