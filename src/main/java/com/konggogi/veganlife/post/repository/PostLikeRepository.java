package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.like.domain.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PostLike p SET p.member = NULL WHERE p.member.id = :memberId")
    void setMemberToNull(Long memberId);
}
