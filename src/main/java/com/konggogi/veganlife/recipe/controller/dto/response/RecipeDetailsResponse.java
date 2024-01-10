package com.konggogi.veganlife.recipe.controller.dto.response;

import com.konggogi.veganlife.member.domain.VegetarianType;
import java.util.List;

public record RecipeDetailsResponse(boolean isLiked,
                                    String name,
                                    List<VegetarianType> recipeTypes,
                                    List<String> imageUrls,
                                    List<String> ingredients,
                                    List<RecipeDescriptionDetailsResponse> descriptions) {}
