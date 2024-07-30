package com.konggogi.veganlife.recipe.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import com.konggogi.veganlife.recipe.service.RecipeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeSearchService recipeSearchService;
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getRecipeList(
            @RequestParam(required = false) VegetarianType vegetarianType,
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(
                recipeSearchService.searchAll(vegetarianType, pageable, userDetails.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsResponse> getRecipeDetails(
            @PathVariable("id") Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(recipeSearchService.search(recipeId, userDetails.id()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addRecipe(
            @Valid @RequestPart RecipeAddRequest request,
            @RequestPart(required = false) @Validated @Size(max = 5) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        recipeService.add(request, images, userDetails.id());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RecipeResponse>> getRecommendedRecipe(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(recipeSearchService.searchRecommended(userDetails.id()));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RecipeResponse>> getRecipeListByKeyword(
            String keyword,
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(
                recipeSearchService.searchAllByKeyword(keyword, pageable, userDetails.id()));
    }
}
