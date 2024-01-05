package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String tagName);

    @Query(
            "SELECT pt.tag FROM PostTag pt "
                    + "GROUP BY pt.tag "
                    + "HAVING COUNT(pt.post) >= 5 "
                    + "ORDER BY COUNT(pt.post) DESC "
                    + "LIMIT 10")
    List<Tag> findPopularTags();
}
