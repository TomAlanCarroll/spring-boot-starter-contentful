package com.tomalancarroll.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.contentful.java.cma.Constants.CMAFieldType.Object;
import static com.contentful.java.cma.Constants.CMAFieldType.Symbol;
import static com.tomalancarroll.service.ContentfulConstants.*;

@Service
public class ContentfulInitializerService {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulInitializerService.class);

    @Value("${contentful.space.id}")
    private String contentfulSpaceId;

    @Autowired
    private CMAClient contentfulManagementClient;

    public void initialize() {
        try {
            if (!contentTypeIsSetup()) {
                setupContentType();
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

    public boolean contentTypeIsSetup() {
        try {
            CMAArray<CMAContentType> result = contentfulManagementClient.contentTypes().fetchAll(contentfulSpaceId);
            logger.info("Fetched all content types, size is: " + ((result != null) ? result.getItems().size() : "0"));

            for (CMAContentType contentType : result.getItems()) {
                if (TRANSLATION_CONTENT_TYPE_NAME.getValue().equals(contentType.getName())) {
                    logger.info("Found Translation type on Contentful");
                    return true;
                }
            }
        } catch (Exception e) {
            throw new SecurityException(e);
        }

        return false;
    }

    public void setupContentType() {
        CMAContentType result = contentfulManagementClient.contentTypes()
                .create(contentfulSpaceId, new CMAContentType().setName("Translation")
                        .addField(new CMAField()
                                .setId(SUBJECT_FIELD_ID.getValue())
                                .setName(SUBJECT_FIELD_NAME.getValue())
                                .setType(Symbol)
                                .setRequired(true))
                        .addField(new CMAField()
                                .setId(DICTIONARY_FIELD_ID.getValue())
                                .setName(DICTIONARY_FIELD_NAME.getValue())
                                .setType(Object)
                                .setLocalized(true)
                                .setRequired(true)))
                        .setDisplayField(SUBJECT_FIELD_ID.getValue());

        logger.info("Successfully created Translation content type");

        // Content Type starts as draft, we must publish
        publishContentType(result);
    }

    public void publishContentType(CMAContentType toPublish) {
        logger.info("Starting publishing of Translation content type");

        contentfulManagementClient.contentTypes().publish(toPublish);

        logger.info("Successfully published Translation content type");
    }
}
