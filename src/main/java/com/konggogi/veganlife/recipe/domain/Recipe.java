package com.konggogi.veganlife.recipe.domain;


import com.konggogi.veganlife.global.domain.TimeStamped;
import com.konggogi.veganlife.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeType> recipeTypes = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeImage> recipeImages = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeDescription> descriptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Recipe(Long id, String name, Member member) {
        this.id = id;
        this.name = name;
        this.member = member;
    }

    public RecipeImage getThumbnailUrl() {

        if (recipeImages.isEmpty()) {
            return null;
        }
        return recipeImages.get(0);
    }

    public void update(
            List<RecipeType> recipeTypes,
            List<RecipeImage> recipeImages,
            List<RecipeIngredient> ingredients,
            List<RecipeDescription> descriptions) {

        updateRecipeTypes(recipeTypes);
        updateRecipeImages(recipeImages);
        updateIngredients(ingredients);
        updateDescriptions(descriptions);
    }

    private void updateRecipeTypes(List<RecipeType> recipeTypes) {
        this.recipeTypes.clear();
        this.recipeTypes.addAll(recipeTypes);
    }

    private void updateRecipeImages(List<RecipeImage> recipeImages) {
        this.recipeImages.clear();
        this.recipeImages.addAll(recipeImages);
    }

    private void updateIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients.clear();
        this.ingredients.addAll(ingredients);
    }

    private void updateDescriptions(List<RecipeDescription> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
    }
}
