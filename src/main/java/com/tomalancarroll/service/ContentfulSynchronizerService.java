package com.tomalancarroll.service;

import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.tomalancarroll.service.ContentfulConstants.*;

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
            String translationContentTypeSysId = getTranslationContentTypeSysId();
            if (getTranslationsThatNeedSynchronization(translationContentTypeSysId).size() > 0) {
                synchronizeTranslations(translationContentTypeSysId);
            } else {
                logger.info("Translation content already synchronized; skipping synchronization");
            }
        } catch (SecurityException e) {
            throw new IllegalStateException("Unable to synchronize Contentful. " +
                    "Please check the application properties contentful.space.id and " +
                    "contentful.management.token", e);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to synchronize Contentful", e);
        }
    }

    public List<Resource> getTranslationsThatNeedSynchronization(String translationContentTypeSysId) {
        try {
            Resource[] resources = dictionaryResolverService.resolveDictionaries();
            List<Resource> toReturn = Arrays.asList(resources);
            CMAArray<CMAEntry> result = contentfulManagementClient.entries().fetchAll(contentfulSpaceId);
            logger.info("Fetched all content entries, size is: " + ((result != null) ? result.getItems().size() : "0"));

            for (CMAEntry entry : result.getItems()) {
                Object field = entry.getFields().get(DICTIONARY_FIELD_NAME.getValue());
                String entryContentTypeId = (String)((LinkedTreeMap)((LinkedTreeMap)entry.getSys().get("contentType")).get("sys")).get("id");

                logger.info("got translation with " + ((field != null) ? field.getClass() : "null"));

                for (Resource resource : resources) {
                    String subject = (String)entry.getFields().get("subject").get("value");
                    if (translationContentTypeSysId.equals(entryContentTypeId) &&
                            resource.getFilename().replace(".json", "").equals(subject)) {
                        logger.info("Found Translation content on Contentful: " + resource.getFilename());
                        toReturn.remove(resource);
                    }
                }
            }

            return toReturn;
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public void synchronizeTranslations(String translationContentTypeSysId) {
        logger.info("Starting creation of Translation content");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("foo", "bar");

        CMAEntry toCreate = new CMAEntry()
                .setField(DICTIONARY_FIELD_ID.getValue(), jsonObject, Locale.US.toLanguageTag())
                .setField(SUBJECT_FIELD_ID.getValue(), "global", Locale.US.toLanguageTag());

        CMAEntry result = contentfulManagementClient.entries()
                .create(contentfulSpaceId, translationContentTypeSysId, toCreate);

        logger.info("Successfully created Translation content");

        publishContent(result);
    }

    public void publishContent(CMAEntry toPublish) {
        logger.info("Starting publishing of Translation content");

        contentfulManagementClient.entries().publish(toPublish);

        logger.info("Successfully published Translation content");
    }

    private String getTranslationContentTypeSysId() {
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
