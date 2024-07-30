package com.konggogi.veganlife.recipe.domain.mapper;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeDetailsResponse;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import java.util.List;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(
            source = "recipe.thumbnail",
            target = "thumbnailUrl",
            qualifiedByName = "recipeImageToImageUrl")
    @Mapping(
            source = "recipe.recipeTypes",
            target = "recipeTypes",
            qualifiedByName = "recipeTypeToVegetarianType")
    @Mapping(source = "recipe.member", target = "author")
    RecipeResponse toRecipeResponse(Recipe recipe, boolean isLiked);

    @Mapping(
            source = "recipe.recipeTypes",
            target = "recipeTypes",
            qualifiedByName = "recipeTypeToVegetarianType")
    @Mapping(
            source = "recipe.recipeImages",
            target = "imageUrls",
            qualifiedByName = "recipeImageToImageUrl")
    @Mapping(
            source = "recipe.ingredients",
            target = "ingredients",
            qualifiedByName = "recipeIngredientToString")
    @Mapping(
            source = "recipe.descriptions",
            target = "descriptions",
            qualifiedByName = "recipeDescriptionToString")
    @Mapping(source = "recipe.member", target = "author")
    RecipeDetailsResponse toRecipeDetailsResponse(Recipe recipe, boolean isLiked);

    default Recipe toEntity(RecipeAddRequest request, List<String> imageUrls, Member member) {

        // TODO: 이 로직이 매핑 로직인지, 서비스 로직인지 모호함, 리팩토링 단계에서 추가적으로 고민
        Recipe recipe = Recipe.builder().name(request.name()).member(member).build();

        List<RecipeType> recipeTypes =
                request.recipeType().stream()
                        .map(vegetarianType -> toRecipeType(vegetarianType, recipe))
                        .toList();
        List<RecipeImage> recipeImages =
                imageUrls.stream().map(imageUrl -> toRecipeImage(imageUrl, recipe)).toList();
        List<RecipeIngredient> ingredients =
                request.ingredients().stream()
                        .map(ingredient -> toRecipeIngredient(ingredient, recipe))
                        .toList();
        List<RecipeDescription> descriptions =
                IntStream.range(0, request.descriptions().size())
                        .mapToObj(
                                idx ->
                                        this.toRecipeDescription(
                                                idx + 1, request.descriptions().get(idx), recipe))
                        .toList();

        // TODO: update 내부에서 Recipe와 Recipe 하위 엔티티의 관계를 설정하도록 수정
        recipe.update(recipeTypes, recipeImages, ingredients, descriptions);

        return recipe;
    }

    @Mapping(target = "id", ignore = true)
    RecipeImage toRecipeImage(String imageUrl, Recipe recipe);

    @Mapping(target = "id", ignore = true)
    RecipeType toRecipeType(VegetarianType vegetarianType, Recipe recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "ingredient", target = "name")
    RecipeIngredient toRecipeIngredient(String ingredient, Recipe recipe);

    @Mapping(target = "id", ignore = true)
    RecipeDescription toRecipeDescription(Integer sequence, String description, Recipe recipe);

    @Named("recipeImageToImageUrl")
    static String recipeImageToImageUrl(RecipeImage recipeImage) {

        return recipeImage.getImageUrl();
    }

    @Named("recipeTypeToVegetarianType")
    static VegetarianType recipeTypeToVegetarianType(RecipeType recipeType) {

        return recipeType.getVegetarianType();
    }

    @Named("recipeIngredientToString")
    static String recipeIngredientToString(RecipeIngredient ingredient) {

        return ingredient.getName();
    }

    @Named("recipeDescriptionToString")
    static String recipeDescriptionToString(RecipeDescription description) {

        return description.getDescription();
    }
}
