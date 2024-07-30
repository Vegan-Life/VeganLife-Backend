package com.konggogi.veganlife.recipe.service;


import com.konggogi.veganlife.global.util.AwsS3Folders;
import com.konggogi.veganlife.global.util.AwsS3Uploader;
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
}
