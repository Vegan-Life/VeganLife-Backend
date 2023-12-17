package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {}
