package com.konggogi.veganlife.post.service;


import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.NotFoundEntityException;
import com.konggogi.veganlife.post.domain.Post;
import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.repository.PostRepository;
import com.konggogi.veganlife.post.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public Post search(Long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
    }

    public Post searchWithMember(Long postId) {
        return postRepository
                .findByIdFetchJoinMember(postId)
                .orElseThrow(() -> new NotFoundEntityException(ErrorCode.NOT_FOUND_POST));
    }

    public Page<Post> searchAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> searchAll(Long memberId, Pageable pageable) {
        return postRepository.findAllByMemberId(memberId, pageable);
    }

    public Page<Post> searchByKeyword(Pageable pageable, String keyword) {
        return postRepository.findByKeyword(keyword, pageable);
    }

    public Page<Post> searchByMemberComments(Long memberId, Pageable pageable) {
        return postRepository.findByMemberComments(memberId, pageable);
    }

    public List<Tag> searchPopularTags() {
        return tagRepository.findPopularTags();
    }
}
