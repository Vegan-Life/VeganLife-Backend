package com.konggogi.veganlife.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;

import com.konggogi.veganlife.global.AwsS3Uploader;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.domain.VegetarianType;
import com.konggogi.veganlife.member.fixture.MemberFixture;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.RecipeDescription;
import com.konggogi.veganlife.recipe.domain.RecipeImage;
import com.konggogi.veganlife.recipe.domain.RecipeIngredient;
import com.konggogi.veganlife.recipe.domain.RecipeType;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapperImpl;
import com.konggogi.veganlife.recipe.repository.RecipeRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock MemberQueryService memberQueryService;
    @Mock RecipeRepository recipeRepository;
    @Spy RecipeMapper recipeMapper = new RecipeMapperImpl();
    @Mock AwsS3Uploader awsS3Uploader;
    @InjectMocks RecipeService recipeService;

    private Member member = MemberFixture.DEFAULT_M.getWithId(1L);

    @Test
    @DisplayName("레시피 등록 테스트")
    void addTest() {

        RecipeAddRequest request =
                new RecipeAddRequest(
                        "표고버섯 탕수",
                        List.of(VegetarianType.LACTO),
                        List.of("표고버섯 5개", "식용유", "시판 탕수육 소스"),
                        List.of("표고버섯을 먹기 좋은 크기로 자릅니다.", "표고버섯을 튀깁니다.", "탕수육 소스와 버무립니다."));
        List<MultipartFile> images =
                List.of(
                        new MockMultipartFile(
                                "images",
                                "image1.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "image1.png".getBytes()));
        List<String> imageUrls = List.of("image1.png");
        willReturn(imageUrls).given(awsS3Uploader).uploadFiles(eq(AwsS3Folders.RECIPE), any());
        Recipe recipe = recipeMapper.toEntity(request, imageUrls, member);

        given(memberQueryService.search(anyLong())).willReturn(member);
        recipeService.add(request, images, member.getId());

        then(memberQueryService).should(times(1)).search(member.getId());
        then(recipeRepository).should(times(1)).save(any(Recipe.class));
        assertThat(recipe.getName()).isEqualTo("표고버섯 탕수");
        assertThat(recipe.getRecipeTypes()).hasSize(1);
        assertThat(recipe.getRecipeTypes().stream().map(RecipeType::getVegetarianType).toList())
                .containsAll(List.of(VegetarianType.LACTO));
        assertThat(recipe.getRecipeImages()).hasSize(1);
        assertThat(recipe.getRecipeImages().stream().map(RecipeImage::getImageUrl))
                .containsAll(List.of("image1.png"));
        assertThat(recipe.getIngredients()).hasSize(3);
        assertThat(recipe.getIngredients().stream().map(RecipeIngredient::getName))
                .containsAll(List.of("표고버섯 5개", "식용유", "시판 탕수육 소스"));
        assertThat(recipe.getDescriptions()).hasSize(3);
        assertThat(recipe.getDescriptions().stream().map(RecipeDescription::getSequence))
                .containsAll(List.of(1, 2, 3));
        assertThat(recipe.getDescriptions().stream().map(RecipeDescription::getDescription))
                .containsAll(List.of("표고버섯을 먹기 좋은 크기로 자릅니다.", "표고버섯을 튀깁니다.", "탕수육 소스와 버무립니다."));
    }
}
