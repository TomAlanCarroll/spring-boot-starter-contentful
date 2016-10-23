package com.tomalancarroll.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
            List<Resource> toSynchronize = getTranslationsThatNeedSynchronization(translationContentTypeSysId);
            if (toSynchronize.size() > 0) {
                synchronizeTranslations(toSynchronize, translationContentTypeSysId);
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

    private List<Resource> getTranslationsThatNeedSynchronization(String translationContentTypeSysId) {
        try {
            Resource[] resources = dictionaryResolverService.resolveDictionaries();
            List<Resource> toReturn = new ArrayList<>();
            CMAArray<CMAEntry> result = contentfulManagementClient.entries().fetchAll(contentfulSpaceId);
            logger.info("Fetched all content entries, size is: " + ((result != null) ? result.getItems().size() : "0"));

            for (Resource resource : resources) {
                boolean add = true;
                String languageTag = getResourceLocale(resource).toLanguageTag();

                for (CMAEntry entry : result.getItems()) {
                    String entrySubject = (String) entry.getFields().get("subject").get(languageTag);
                    String entryContentTypeId = (String) ((LinkedTreeMap) ((LinkedTreeMap) entry.getSys().get("contentType")).get("sys")).get("id");

                    // If contentType is equivalent and subject is equivalent then we don't need to synchronize this resource
                    if (translationContentTypeSysId.equals(entryContentTypeId) &&
                            getResourceSubject(resource).equals(entrySubject)) {
                        add = false;
                        logger.info("Skipping synchronization for found Translation content on Contentful: " + resource.getFilename());
                    }
                }

                if (add) {
                    toReturn.add(resource);
                    logger.info("Synchronization required for missing Translation content on Contentful: " + resource.getFilename());
                }
            }

            return toReturn;
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    private Locale getResourceLocale(Resource resource) {
        try {
            return Locale.forLanguageTag(resource.getFile().getParentFile().getName());
        } catch (IOException e) {
            logger.error("Unable to obtain locale for resource " + resource.getFilename(), e);
            return Locale.US;
        }
    }

    private String getResourceSubject(Resource resource) {
        return resource.getFilename().replace(".json", "");
    }

    // TODO: Support updates of JSON files; only new files will be synchronized
    private void synchronizeTranslations(List<Resource> toSynchronize, String translationContentTypeSysId) {
        logger.info("Starting creation of Translation content");

        for (Resource resource : toSynchronize) {
            JsonObject jsonObject = loadJson(resource);
            String languageTag = getResourceLocale(resource).toLanguageTag();

            CMAEntry toCreate = new CMAEntry()
                    .setField(DICTIONARY_FIELD_ID.getValue(), jsonObject, languageTag)
                    .setField(SUBJECT_FIELD_ID.getValue(), getResourceSubject(resource), languageTag);

            CMAEntry result = contentfulManagementClient.entries()
                    .create(contentfulSpaceId, translationContentTypeSysId, toCreate);

            logger.info("Successfully created Translation content: " + resource.getFilename());

            publishContent(result);
        }

    }

    private JsonObject loadJson(Resource resource) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(new InputStreamReader(resource.getInputStream()));
            return jsonElement.getAsJsonObject();
        } catch (IOException e) {
            logger.error("Unable to load resource " + resource.getFilename(), e);
            throw new IllegalStateException("Unable to load resource " + resource.getFilename());
        } catch (IllegalArgumentException e) {
            logger.error("Non JSON Object encountered in resource file " + resource.getFilename(), e);
            throw e;
        }
    }

    private void publishContent(CMAEntry toPublish) {
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
