package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.member = NULL WHERE p.member.id = :memberId")
    void setMemberToNull(Long memberId);
}
