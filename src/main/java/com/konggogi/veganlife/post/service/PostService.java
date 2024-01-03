package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.controller.dto.request.PostAddRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostImageMapper;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.domain.mapper.TagMapper;
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
    private final TagMapper tagMapper;
    private final PostImageMapper postImageMapper;

    public Post add(Long memberId, PostAddRequest postAddRequest) {
        Member member = memberQueryService.search(memberId);
        Post post = postMapper.toEntity(member, postAddRequest);
        addTags(post, postAddRequest.tags());
        addImages(post, postAddRequest.imageUrls());
        return postRepository.save(post);
    }

    public void removeMemberFromPost(Long memberId) {
        postRepository.setMemberToNull(memberId);
    }

    private void addTags(Post post, List<String> tagNames) {
        tagNames.stream()
                .distinct()
                .map(
                        tagName ->
                                tagRepository
                                        .findByName(tagName)
                                        .orElseGet(
                                                () -> {
                                                    Tag tag = tagMapper.toEntity(tagName);
                                                    return tagRepository.save(tag);
                                                }))
                .map(tagMapper::toPostTag)
                .forEach(post::addPostTag);
    }

    private void addImages(Post post, List<String> imageUrls) {
        imageUrls.stream().map(postImageMapper::toEntity).forEach(post::addPostImage);
    }
}
