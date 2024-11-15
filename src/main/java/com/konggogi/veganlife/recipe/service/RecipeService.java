package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.global.AwsS3Uploader;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.recipe.controller.dto.request.RecipeAddRequest;
import com.konggogi.veganlife.recipe.domain.Recipe;
import com.konggogi.veganlife.recipe.domain.mapper.RecipeMapper;
import com.konggogi.veganlife.recipe.repository.RecipeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final MemberQueryService memberQueryService;
    private final RecipeRepository recipeRepository;

    private final RecipeMapper recipeMapper;

    private final AwsS3Uploader awsS3Uploader;

    public void add(RecipeAddRequest request, List<MultipartFile> images, Long memberId) {

        List<String> imageUrls = awsS3Uploader.uploadFiles(AwsS3Folders.RECIPE, images);
        Recipe recipe =
                recipeMapper.toEntity(request, imageUrls, memberQueryService.search(memberId));
        recipeRepository.save(recipe);
    }

    public void modify(
            Long recipeId, RecipeAddRequest request, List<MultipartFile> images, Long memberId) {

        Recipe recipe =
                recipeRepository
                        .findById(recipeId)
                        .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));
        recipe.checkOwner(memberId);

        List<String> imageUrls = awsS3Uploader.uploadFiles(AwsS3Folders.RECIPE, images);
        Recipe updated = recipeMapper.toEntity(request, imageUrls, recipe.getMember());
        recipe.update(
                updated.getRecipeTypes(),
                updated.getRecipeImages(),
                updated.getIngredients(),
                updated.getDescriptions());
    }

    public void remove(Long recipeId, Long memberId) {
        Recipe recipe =
                recipeRepository
                        .findById(recipeId)
                        .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_RECIPE));
        recipe.checkOwner(memberId);
        recipeRepository.delete(recipe);
    }
}
