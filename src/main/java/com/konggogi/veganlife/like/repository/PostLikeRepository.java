package com.konggogi.veganlife.like.repository;


import com.konggogi.veganlife.like.domain.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberIdAndId(Long memberId, Long postId);
}
