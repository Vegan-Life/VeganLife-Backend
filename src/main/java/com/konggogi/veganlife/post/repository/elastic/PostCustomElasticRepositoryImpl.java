package com.konggogi.veganlife.post.repository.elastic;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import com.konggogi.veganlife.global.exception.ElasticsearchOperationException;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.post.domain.document.PostDocument;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCustomElasticRepositoryImpl implements PostCustomElasticRepository {
    private final ElasticsearchClient elasticsearchClient;
    private final String POSTS_INDEX = "posts";

    @Override
    public List<PostDocument> findAutoCompleteSuggestion(String keyword, int size) {
        try {
            SearchResponse<PostDocument> response =
                    elasticsearchClient.search(
                            s -> autoCompleteSuggestionQuery(s, keyword, size), PostDocument.class);

            return response.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            throw new ElasticsearchOperationException(ErrorCode.ES_OPERATION_FAILED);
        }
    }

    private ObjectBuilder<SearchRequest> autoCompleteSuggestionQuery(
            SearchRequest.Builder builder, String keyword, int size) {
        Query matchPhraseQuery =
                MatchPhraseQuery.of(mp -> mp.field("title").query(keyword).slop(1).boost(3.0f))
                        ._toQuery();
        Query matchNoriQuery =
                MatchQuery.of(
                                m ->
                                        m.field("title.nori")
                                                .query(keyword)
                                                .operator(Operator.And)
                                                .boost(2.0f)
                                                .fuzziness("1"))
                        ._toQuery();
        Query matchNgramQuery =
                MatchQuery.of(m -> m.field("title.ngram").query(keyword))._toQuery();
        Query boolQuery =
                BoolQuery.of(
                                b ->
                                        b.should(matchPhraseQuery)
                                                .should(matchNoriQuery)
                                                .should(matchNgramQuery)
                                                .minimumShouldMatch("1"))
                        ._toQuery();

        return builder.index(POSTS_INDEX).size(size).query(boolQuery);
    }
}
