package com.konggogi.veganlife.member.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.meallog.controller.dto.response.DailyMealLogListResponse;
import com.konggogi.veganlife.meallog.service.MealLogQueryService;
import com.konggogi.veganlife.member.controller.dto.response.ProfileResponse;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.member.service.MemberService;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.service.PostQueryService;
import com.konggogi.veganlife.recipe.controller.dto.response.RecipeResponse;
import com.konggogi.veganlife.recipe.service.RecipeSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final MealLogQueryService mealLogQueryService;
    private final PostQueryService postQueryService;
    private final RecipeSearchService recipeSearchService;

    @DeleteMapping()
    public ResponseEntity<Void> removeMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.removeMember(userDetails.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ProfileResponse> getMembersProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(ProfileResponse.from(memberQueryService.search(memberId)));
    }

    @GetMapping("/{memberId}/meal-log")
    public ResponseEntity<List<DailyMealLogListResponse>> getMembersMealLogList(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                mealLogQueryService.searchWeeklyMealLogs(memberId).entrySet().stream()
                        .map(DailyMealLogListResponse::from)
                        .toList());
    }

    @GetMapping("/{memberId}/posts")
    public ResponseEntity<Page<PostSimpleResponse>> getMembersPostList(
            @PathVariable Long memberId, Pageable pageable) {
        return ResponseEntity.ok(
                postQueryService.searchAll(memberId, pageable).map(PostSimpleResponse::from));
    }

    @GetMapping("/{memberId}/recipes")
    public ResponseEntity<Page<RecipeResponse>> getMembersRecipeList(
            @PathVariable Long memberId, Pageable pageable) {
        return ResponseEntity.ok(recipeSearchService.searchAllByMemberId(memberId, pageable));
    }
}
