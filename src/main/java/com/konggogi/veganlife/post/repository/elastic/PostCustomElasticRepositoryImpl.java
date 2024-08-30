package com.konggogi.veganlife.post.repository.elastic;


import com.konggogi.veganlife.post.domain.document.PostDocument;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCustomElasticRepositoryImpl implements PostCustomElasticRepository {
    private final ElasticsearchOperations elasticsearchOperations;
    private final String POSTS_INDEX = "posts";

    @Override
    public List<PostDocument> findAutoCompleteSuggestion(String keyword, int size) {
        SearchHits<PostDocument> response =
                elasticsearchOperations.search(
                        autoCompleteSuggestionQuery(keyword, size),
                        PostDocument.class,
                        IndexCoordinates.of(POSTS_INDEX));

        return response.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    private Query autoCompleteSuggestionQuery(String keyword, int size) {
        QueryBuilder matchPhraseQuery =
                QueryBuilders.matchPhraseQuery("title", keyword).slop(1).boost(5.0f);

        QueryBuilder matchNoriQuery =
                QueryBuilders.matchQuery("title.nori", keyword)
                        .operator(Operator.AND)
                        .boost(2.0f)
                        .fuzziness(Fuzziness.ONE);

        QueryBuilder matchNgramQuery = QueryBuilders.matchQuery("title.ngram", keyword);

        QueryBuilder boolQuery =
                QueryBuilders.boolQuery()
                        .should(matchPhraseQuery)
                        .should(matchNoriQuery)
                        .should(matchNgramQuery)
                        .minimumShouldMatch(1);

        return new NativeSearchQueryBuilder().withQuery(boolQuery).withMaxResults(size).build();
    }
}
