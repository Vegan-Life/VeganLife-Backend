package com.konggogi.veganlife.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.konggogi.veganlife.post.domain.Tag;
import com.konggogi.veganlife.post.fixture.TagFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TagRepositoryTest {
    @Autowired TagRepository tagRepository;

    private final Tag tag = TagFixture.DEFAULT.getTag();

    @BeforeEach
    void setup() {
        tagRepository.save(tag);
    }

    @Test
    void findByNameTag() {
        // when
        String tagName = tag.getName();
        Optional<Tag> foundTag = tagRepository.findByName(tagName);
        // then
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getName()).isEqualTo(tagName);
    }
}
