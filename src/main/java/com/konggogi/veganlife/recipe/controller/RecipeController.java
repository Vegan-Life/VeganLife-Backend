package com.konggogi.veganlife.recipe.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import com.konggogi.veganlife.recipe.service.RecipeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeSearchService recipeSearchService;
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getRecipeList(
            VegetarianType vegetarianType, Pageable pageable) {

        return ResponseEntity.ok(recipeSearchService.searchAll(vegetarianType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsResponse> getRecipeDetails(@PathVariable Long id) {

        return ResponseEntity.ok(recipeSearchService.search(id));
    }

    @PostMapping
    public ResponseEntity<Void> addRecipe(
            @Valid @RequestBody RecipeAddRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        recipeService.add(request, userDetails.id());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RecipeResponse>> getRecommendedRecipe(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(recipeSearchService.searchRecommended(userDetails.id()));
    }
}
