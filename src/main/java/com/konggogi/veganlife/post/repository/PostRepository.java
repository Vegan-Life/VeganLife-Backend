package com.konggogi.veganlife.post.repository;


import com.konggogi.veganlife.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {}
