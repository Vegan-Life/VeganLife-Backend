package com.konggogi.veganlife.recipe.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.repository.MemberRepository;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import com.konggogi.veganlife.recipe.fixture.RecipeDescriptionFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeImageFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeIngredientsFixture;
import com.konggogi.veganlife.recipe.fixture.RecipeTypeFixture;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing(setDates = false)
public class RecipeRepositoryTest {

    @Autowired RecipeRepository recipeRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    private final Member member = MemberFixture.DEFAULT_M.get();

    @BeforeEach
    void setup() {
        memberRepository.save(member);
    }

    @Test
    @DisplayName("VegetarianType에 해당하는 Recipe 레코드 목록을 조회한다.")
    void findAllByRecipeTypesTest() {

        List<RecipeType> recipeTypes1 =
                List.of(RecipeTypeFixture.OVO.get(), RecipeTypeFixture.LACTO.get());
        Recipe recipe1 = createRecipe(recipeTypes1);
        recipeRepository.save(recipe1);

        List<RecipeType> recipeTypes2 = List.of(RecipeTypeFixture.OVO.get());
        Recipe recipe2 = createRecipe(recipeTypes2);
        recipeRepository.save(recipe2);

        Pageable pageable = Pageable.ofSize(20);
        Page<Recipe> recipes1 = recipeRepository.findAllByRecipeTypes(VegetarianType.OVO, pageable);
        Page<Recipe> recipes2 =
                recipeRepository.findAllByRecipeTypes(VegetarianType.LACTO, pageable);

        assertThat(recipes1.getNumberOfElements()).isEqualTo(2);
        assertThat(recipes2.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("VegetarianType이 null이라면 전체 Recipe 레코드 목록을 조회한다.")
    void findAllIfVegetarianTypeTest() {

        List<RecipeType> recipeTypes1 =
                List.of(RecipeTypeFixture.OVO.get(), RecipeTypeFixture.LACTO.get());
        Recipe recipe1 = createRecipe(recipeTypes1);
        recipeRepository.save(recipe1);

        List<RecipeType> recipeTypes2 = List.of(RecipeTypeFixture.OVO.get());
        Recipe recipe2 = createRecipe(recipeTypes2);
        recipeRepository.save(recipe2);

        Pageable pageable = Pageable.ofSize(20);
        Page<Recipe> recipes = recipeRepository.findAllByRecipeTypes(null, pageable);

        assertThat(recipes.getNumberOfElements()).isEqualTo(2);
    }

    private Recipe createRecipe(List<RecipeType> recipeType) {

        List<RecipeType> recipeTypes = new ArrayList<>(recipeType);

        List<RecipeImage> recipeImages =
                List.of(
                        RecipeImageFixture.DEFAULT.get(),
                        RecipeImageFixture.DEFAULT.get(),
                        RecipeImageFixture.DEFAULT.get());

        List<RecipeIngredient> ingredients =
                List.of(
                        RecipeIngredientsFixture.DEFAULT.get(),
                        RecipeIngredientsFixture.DEFAULT.get(),
                        RecipeIngredientsFixture.DEFAULT.get());

        List<RecipeDescription> descriptions =
                List.of(
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get(),
                        RecipeDescriptionFixture.DEFAULT.get());

        return RecipeFixture.DEFAULT.get(
                recipeTypes, recipeImages, ingredients, descriptions, member);
    }
}
