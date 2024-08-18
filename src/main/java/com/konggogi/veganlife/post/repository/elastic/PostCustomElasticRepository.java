package com.konggogi.veganlife.post.repository.elastic;


import com.konggogi.veganlife.post.domain.document.PostDocument;
import java.util.List;

public interface PostCustomElasticRepository {
    List<PostDocument> findAutoCompleteSuggestion(String keyword, int size);
}
