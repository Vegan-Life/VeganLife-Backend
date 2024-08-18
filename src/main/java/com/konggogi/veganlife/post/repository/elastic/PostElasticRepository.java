package com.konggogi.veganlife.post.repository.elastic;


import com.konggogi.veganlife.post.domain.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostElasticRepository extends ElasticsearchRepository<PostDocument, Long> {}
