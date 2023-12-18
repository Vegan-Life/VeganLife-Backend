package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String tagName);
}
