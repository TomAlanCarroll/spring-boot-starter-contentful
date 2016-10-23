package com.tomalancarroll.service;

import com.contentful.java.cma.CMACallback;
import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ContentfulSynchronizerService {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulSynchronizerService.class);

    @Value("${contentful.space.id}")
    private String contentfulSpaceId;

    @Autowired
    private CMAClient contentfulManagementClient;

    @Autowired
    private DictionaryResolverService dictionaryResolverService;

    public void synchronize() {
        try {
            if (!translationsAreSynchronized()) {
                synchronizeTranslations();
            } else {
                logger.info("Translation type already exists; skipping intialization");
            }
        } catch (SecurityException e) {
            throw new IllegalStateException("Unable to initialize Contentful. " +
                    "Please check the application properties contentful.space.id and " +
                    "contentful.management.token", e);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize Contentful", e);
        }
    }

    public boolean translationsAreSynchronized() {
        try {
            Resource[] resources = dictionaryResolverService.resolveDictionaries();
//            CMAArray<CMAEntry> result = contentfulManagementClient.entries().fetchAll(contentfulSpaceId);
//            logger.info("Fetched all content types, size is: " + ((result != null) ? result.getItems().size() : "0"));
//
//            for (CMAEntry entry : result.getItems()) {
//                if (TRANSLATION_CONTENT_TYPE_NAME.getValue().equals(contentType.getName())) {
//                    logger.info("Found Translation type on Contentful");
//                    return true;
//                }
//            }
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        return false;
    }

    public void synchronizeTranslations() {
        // TODO: synchronize
//        contentfulManagementClient.contentTypes()
//                .async()
//                .create(contentfulSpaceId,
//                        new CMAContentType().setName("Translation")
//                                .addField(new CMAField()
//                                        .setId("dictionary")
//                                        .setName("Dictionary")
//                                        .setType(Object)
//                                        .setRequired(true)),
//                        new CMACallback<CMAContentType>() {
//                            @Override
//                            protected void onSuccess(CMAContentType result) {
//                                logger.info("Successfully created Translation content type");
//
//                                // Content Type starts as draft, we must publish
//                                publishContentType(result);
//                            }
//                        });
    }

    public void publishContentType(CMAContentType toPublish) {
        logger.info("Starting publishing of Translation content type");

        contentfulManagementClient.contentTypes()
                .async()
                .publish(toPublish, new CMACallback<CMAContentType>() {
                    @Override
                    protected void onSuccess(CMAContentType result) {
                        logger.info("Successfully published Translation content type");
                    }
                });
    }
}
