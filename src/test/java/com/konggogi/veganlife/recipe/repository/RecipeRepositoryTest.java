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
import com.konggogi.veganlife.recipe.fixture.RecipeIngredientFixture;
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

    @Test
    @DisplayName("id 기반으로 Recipe 레코드를 조회한다. - RecipeDescription은 sequence로 정렬된다.")
    void findByIdTest() {

        Recipe recipe = createRecipe(List.of(RecipeTypeFixture.OVO.get()));
        recipeRepository.save(recipe);
        em.clear();

        Recipe found = recipeRepository.findById(recipe.getId()).get();

        assertThat(found.getDescriptions().get(0).getSequence()).isEqualTo(1);
        assertThat(found.getDescriptions().get(1).getSequence()).isEqualTo(2);
        assertThat(found.getDescriptions().get(2).getSequence()).isEqualTo(3);
    }

    @Test
    @DisplayName("VegetarianType에 해당하는 Recipe 레코드 개수를 조회한다.")
    void countByRecipeTypeTest() {

        List<Recipe> recipes =
                List.of(
                        createRecipe(
                                List.of(
                                        RecipeTypeFixture.OVO.get(),
                                        RecipeTypeFixture.LACTO.get())),
                        createRecipe(
                                List.of(
                                        RecipeTypeFixture.OVO.get(),
                                        RecipeTypeFixture.PESCO.get())),
                        createRecipe(
                                List.of(
                                        RecipeTypeFixture.OVO.get(),
                                        RecipeTypeFixture.LACTO.get())));
        recipeRepository.saveAll(recipes);

        int ovoTotal = recipeRepository.countByRecipeType(VegetarianType.OVO);
        int lactoTotal = recipeRepository.countByRecipeType(VegetarianType.LACTO);
        int pescoTotal = recipeRepository.countByRecipeType(VegetarianType.PESCO);

        assertThat(ovoTotal).isEqualTo(3);
        assertThat(lactoTotal).isEqualTo(2);
        assertThat(pescoTotal).isEqualTo(1);
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
                        RecipeIngredientFixture.DEFAULT.get(),
                        RecipeIngredientFixture.DEFAULT.get(),
                        RecipeIngredientFixture.DEFAULT.get());

        List<RecipeDescription> descriptions =
                List.of(
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(2, "표고버섯을 튀긴다."),
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(1, "표고버섯을 먹기 좋은 크기로 썬다."),
                        RecipeDescriptionFixture.DEFAULT.getWithDesc(3, "탕수육 소스와 버무린다."));

        return RecipeFixture.DEFAULT.get(
                recipeTypes, recipeImages, ingredients, descriptions, member);
    }
}
