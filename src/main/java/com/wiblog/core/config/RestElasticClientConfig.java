//package com.wiblog.core.config;
//
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//
///**
// * @author panweimin
// */
//@Configuration
//public class RestElasticClientConfig extends AbstractElasticsearchConfiguration {
//
////    @Value("${spring.elasticsearch.rest.username}")
////    private String USERNAME;
////    @Value("${spring.elasticsearch.rest.password}")
////    private String PASSWORD;
//    @Value("${spring.elasticsearch.rest.uris}")
//    private String URLS;
//
//    public static final RequestOptions COMMON_OPTIONS;
//    static {
//        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        COMMON_OPTIONS = builder.build();
//    }
//
//    @Override
//    @Bean(name = "elasticClient")
//    public RestHighLevelClient elasticsearchClient() {
//
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(URLS)
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }
//}
