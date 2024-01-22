package com.konggogi.veganlife.recipe.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.recipe.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes/{id}/likes")
@RequiredArgsConstructor
public class RecipeLikeController {

    private final RecipeLikeService recipeLikeService;

    @PostMapping
    public ResponseEntity<Void> addRecipeLike(
            @PathVariable("id") Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        recipeLikeService.add(recipeId, userDetails.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeRecipeLike(
            @PathVariable("id") Long recipeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        recipeLikeService.remove(recipeId, userDetails.id());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
