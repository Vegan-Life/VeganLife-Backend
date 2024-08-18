package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.global.util.AwsS3Folders;
import com.konggogi.veganlife.global.util.AwsS3Uploader;
import com.konggogi.veganlife.member.domain.Member;
import com.konggogi.veganlife.member.service.MemberQueryService;
import com.konggogi.veganlife.post.controller.dto.request.PostFormRequest;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.PostImage;
import com.konggogi.veganlife.post.domain.PostTag;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.domain.mapper.PostImageMapper;
import com.konggogi.veganlife.post.domain.mapper.PostMapper;
import com.konggogi.veganlife.post.domain.mapper.TagMapper;
import com.konggogi.veganlife.post.repository.PostRepository;
import com.konggogi.veganlife.post.repository.TagRepository;
import com.konggogi.veganlife.post.repository.elastic.PostElasticRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final MemberQueryService memberQueryService;
    private final PostRepository postRepository;
    private final PostElasticRepository postElasticRepository;
    private final PostQueryService postQueryService;
    private final TagRepository tagRepository;

    private final PostMapper postMapper;
    private final TagMapper tagMapper;
    private final PostImageMapper postImageMapper;

    private final AwsS3Uploader awsS3Uploader;

    public Post add(Long memberId, PostFormRequest postFormRequest, List<MultipartFile> images) {
        Member member = memberQueryService.search(memberId);
        Post post = postMapper.toEntity(member, postFormRequest);
        List<String> imageUrls = awsS3Uploader.uploadFiles(AwsS3Folders.COMMUNITY, images);
        mapToPostTag(postFormRequest.tags()).forEach(post::addPostTag);
        mapToPostImage(imageUrls).forEach(post::addPostImage);
        postRepository.save(post);
        postElasticRepository.save(postMapper.toPostDocument(post));
        return post;
    }

    public void removeMemberFromPost(Long memberId) {
        postRepository.setMemberToNull(memberId);
    }

    public void modify(
            Long memberId,
            Long postId,
            PostFormRequest postFormRequest,
            List<MultipartFile> multipartFiles) {
        memberQueryService.search(memberId);
        Post post = postQueryService.search(postId);
        List<String> imageUrls = awsS3Uploader.uploadFiles(AwsS3Folders.COMMUNITY, multipartFiles);
        List<PostImage> postImages = mapToPostImage(imageUrls);
        List<PostTag> tags = mapToPostTag(postFormRequest.tags());
        post.update(postFormRequest.title(), postFormRequest.content(), postImages, tags);
        postElasticRepository.save(postMapper.toPostDocument(post));
    }

    public void remove(Long postId) {
        Post post = postQueryService.search(postId);
        postRepository.delete(post);
        postElasticRepository.deleteById(postId);
    }

    private List<PostTag> mapToPostTag(List<String> tagNames) {
        return tagNames.stream().distinct().map(this::addTag).map(tagMapper::toPostTag).toList();
    }

    private Tag addTag(String tagName) {
        return tagRepository
                .findByName(tagName)
                .orElseGet(
                        () -> {
                            Tag tag = tagMapper.toEntity(tagName);
                            return tagRepository.save(tag);
                        });
    }

    private List<PostImage> mapToPostImage(List<String> imageUrls) {
        return imageUrls.stream().map(postImageMapper::toEntity).toList();
    }
}
