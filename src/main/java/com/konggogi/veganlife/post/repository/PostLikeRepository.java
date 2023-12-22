package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberIdAndId(Long postId, Long memberId);
}
