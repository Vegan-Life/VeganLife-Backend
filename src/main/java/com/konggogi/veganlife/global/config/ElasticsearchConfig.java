package com.konggogi.veganlife.global.config;


import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.konggogi.veganlife.*.repository.elastic")
public class ElasticsearchConfig extends AbstractOpenSearchConfiguration {

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private Integer port;

    @Override
    public RestHighLevelClient opensearchClient() {

        ClientConfiguration clientConfiguration =
                ClientConfiguration.builder()
                        .connectedTo(host + ":" + port)
                        .usingSsl()
                        .withBasicAuth(username, password)
                        .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
