package com.konggogi.veganlife.post.controller;


import com.konggogi.veganlife.global.security.user.UserDetailsImpl;
import com.konggogi.veganlife.post.controller.dto.request.PostFormRequest;
import com.konggogi.veganlife.post.controller.dto.response.PostAddResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostDetailsResponse;
import com.konggogi.veganlife.post.controller.dto.response.PostSimpleResponse;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.service.PostSearchService;
import com.konggogi.veganlife.post.service.PostService;
import com.konggogi.veganlife.post.service.dto.PostDetailsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostSearchService postSearchService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<PostAddResponse> addPost(
            @RequestPart @Valid PostFormRequest postFormRequest,
            @RequestPart @Size(max = 5) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.add(userDetails.id(), postFormRequest, images);
        return ResponseEntity.ok(postMapper.toPostAddResponse(post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailsResponse> getPost(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostDetailsDto postDetailsDto =
                postSearchService.searchDetailsById(userDetails.id(), postId);
        return ResponseEntity.ok(postMapper.toPostDetailsResponse(postDetailsDto));
    }

    @GetMapping()
    public ResponseEntity<Page<PostSimpleResponse>> getPostList(Pageable pageable) {
        Page<PostSimpleResponse> postSimpleResponsePage =
                postSearchService.searchAllSimple(pageable).map(postMapper::toPostSimpleResponse);
        return ResponseEntity.ok(postSimpleResponsePage);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> modifyPost(
            @PathVariable Long postId,
            @RequestPart PostFormRequest postFormRequest,
            @RequestPart @Size(max = 5) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.modify(userDetails.id(), postId, postFormRequest, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable Long postId) {
        postService.remove(postId);
        return ResponseEntity.noContent().build();
    }
}
