package com.konggogi.veganlife.recipe.fixture;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import java.lang.reflect.Field;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public enum RecipeFixture {
    DEFAULT("표고버섯 탕수육");

    private String name;

    RecipeFixture(String name) {
        this.name = name;
    }

    public Recipe get(
            List<RecipeType> recipeTypes,
            List<RecipeImage> recipeImages,
            List<RecipeIngredient> ingredients,
            List<RecipeDescription> descriptions,
            Member member) {

        Recipe recipe = Recipe.builder().name(name).member(member).build();

        recipeTypes.forEach(recipeType -> setRecipe(recipeType, recipe));
        recipeImages.forEach(recipeImage -> setRecipe(recipeImage, recipe));
        ingredients.forEach(ingredient -> setRecipe(ingredient, recipe));
        descriptions.forEach(description -> setRecipe(description, recipe));
        recipe.update(recipeTypes, recipeImages, ingredients, descriptions);

        return recipe;
    }

    public Recipe get(
            Long id,
            List<RecipeType> recipeTypes,
            List<RecipeImage> recipeImages,
            List<RecipeIngredient> ingredients,
            List<RecipeDescription> descriptions,
            Member member) {

        Recipe recipe = Recipe.builder().id(id).name(name).member(member).build();

        recipeTypes.forEach(recipeType -> setRecipe(recipeType, recipe));
        recipeImages.forEach(recipeImage -> setRecipe(recipeImage, recipe));
        ingredients.forEach(ingredient -> setRecipe(ingredient, recipe));
        descriptions.forEach(description -> setRecipe(description, recipe));
        recipe.update(recipeTypes, recipeImages, ingredients, descriptions);

        return recipe;
    }

    public Recipe getWithName(
            String name,
            List<RecipeType> recipeTypes,
            List<RecipeImage> recipeImages,
            List<RecipeIngredient> ingredients,
            List<RecipeDescription> descriptions,
            Member member) {

        Recipe recipe = Recipe.builder().name(name).member(member).build();

        recipeTypes.forEach(recipeType -> setRecipe(recipeType, recipe));
        recipeImages.forEach(recipeImage -> setRecipe(recipeImage, recipe));
        ingredients.forEach(ingredient -> setRecipe(ingredient, recipe));
        descriptions.forEach(description -> setRecipe(description, recipe));
        recipe.update(recipeTypes, recipeImages, ingredients, descriptions);

        return recipe;
    }

    public Recipe getWithName(
            Long id,
            String name,
            List<RecipeType> recipeTypes,
            List<RecipeImage> recipeImages,
            List<RecipeIngredient> ingredients,
            List<RecipeDescription> descriptions,
            Member member) {

        Recipe recipe = Recipe.builder().id(id).name(name).member(member).build();

        recipeTypes.forEach(recipeType -> setRecipe(recipeType, recipe));
        recipeImages.forEach(recipeImage -> setRecipe(recipeImage, recipe));
        ingredients.forEach(ingredient -> setRecipe(ingredient, recipe));
        descriptions.forEach(description -> setRecipe(description, recipe));
        recipe.update(recipeTypes, recipeImages, ingredients, descriptions);

        return recipe;
    }

    private void setRecipe(RecipeType recipeType, Recipe recipe) {
        Field recipeField = ReflectionUtils.findField(RecipeType.class, "recipe");
        ReflectionUtils.makeAccessible(recipeField);
        ReflectionUtils.setField(recipeField, recipeType, recipe);
    }

    private void setRecipe(RecipeImage recipeImage, Recipe recipe) {
        Field recipeField = ReflectionUtils.findField(RecipeImage.class, "recipe");
        ReflectionUtils.makeAccessible(recipeField);
        ReflectionUtils.setField(recipeField, recipeImage, recipe);
    }

    private void setRecipe(RecipeIngredient ingredient, Recipe recipe) {
        Field recipeField = ReflectionUtils.findField(RecipeIngredient.class, "recipe");
        ReflectionUtils.makeAccessible(recipeField);
        ReflectionUtils.setField(recipeField, ingredient, recipe);
    }

    private void setRecipe(RecipeDescription description, Recipe recipe) {
        Field recipeField = ReflectionUtils.findField(RecipeDescription.class, "recipe");
        ReflectionUtils.makeAccessible(recipeField);
        ReflectionUtils.setField(recipeField, description, recipe);
    }
}
