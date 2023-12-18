package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.repository.PostRepository;
import com.konggogi.veganlife.post.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final MemberQueryService memberQueryService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;

    public Post add(Long memberId, PostAddRequest postAddRequest) {
        Member member = memberQueryService.search(memberId);
        Post post = postMapper.toEntity(member, postAddRequest);
        addTags(post, postAddRequest.tags());
        addImages(post, postAddRequest.imageUrls());
        return postRepository.save(post);
    }

    private void addTags(Post post, List<String> tagNames) {
        List<Tag> tags =
                tagNames.stream()
                        .distinct()
                        .map(
                                tagName ->
                                        tagRepository
                                                .findByName(tagName)
                                                .orElseGet(
                                                        () -> tagRepository.save(new Tag(tagName))))
                        .toList();
        tags.forEach(post::addPostTag);
    }

    private void addImages(Post post, List<String> imageUrls) {
        imageUrls.forEach(post::addPostImage);
    }
}
