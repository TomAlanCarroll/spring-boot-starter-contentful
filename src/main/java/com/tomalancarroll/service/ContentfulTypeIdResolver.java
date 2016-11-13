package com.tomalancarroll.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.tomalancarroll.service.ContentfulConstants.TRANSLATION_CONTENT_TYPE_NAME;

@Service
public class ContentfulTypeIdResolver {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulTypeIdResolver.class);

    @Value("${contentful.space.id}")
    private String contentfulSpaceId;

    @Autowired
    private CMAClient contentfulManagementClient;

    @Cacheable("contentful")
    public String getTranslationContentTypeId() {
        try {
            // Wait 3 seconds; There were race condition issues with Contentful when fetching immediately
            Thread.sleep(3000);

            CMAArray<CMAContentType> result = contentfulManagementClient.contentTypes().fetchAll(contentfulSpaceId);
            logger.info("Fetched all content types, size is: " + ((result != null) ? result.getItems().size() : "0"));

            for (CMAContentType contentType : result.getItems()) {
                if (TRANSLATION_CONTENT_TYPE_NAME.getValue().equals(contentType.getName())) {
                    logger.info("Found Translation type on Contentful for content sychronization");
                    return contentType.getResourceId();
                }
            }
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        throw new IllegalStateException("Unable to retrieve Translation content type sys.id");
    }
}
