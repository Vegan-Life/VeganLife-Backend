package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select distinct p from Post p join fetch p.member " + "where p.id = :postId")
    Optional<Post> findByIdFetchJoinMember(Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Post p SET p.member = NULL WHERE p.member.id = :memberId")
    void setMemberToNull(Long memberId);

    Page<Post> findAll(Pageable pageable);
}
