package com.tomalancarroll.config;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cma.CMAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentfulConfig {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulConfig.class);

    @Value("${contentful.space.id}")
    private String contentfulSpaceId;

    @Value("${contentful.delivery.api.key}")
    private String contentfulDeliveryApiKey;

    @Value("${contentful.preview.api.key}")
    private String contentfulPreviewApiKey;

    @Value("${contentful.management.token}")
    private String contentfulManagementToken;

    @Bean("contentfulDeliveryClient")
    public CDAClient contentfulDeliveryClient() {
        logger.info("Contentful Delivery Client is being configured");
        return CDAClient.builder()
                .setSpace(contentfulSpaceId)
                .setToken(contentfulDeliveryApiKey)
                .build();
    }

    @Bean("contentfulPreviewClient")
    public CDAClient contentfulPreviewClient() {
        logger.info("Contentful Preview Client is being configured");
        return CDAClient.builder()
                .setSpace(contentfulSpaceId)
                .setToken(contentfulPreviewApiKey)
                .build();
    }

    @Bean
    public CMAClient contentfulManagementClient() {
        logger.info("Contentful Management Client is being configured");
        return new CMAClient.Builder()
                .setAccessToken(contentfulManagementToken)
                .build();
    }
}
