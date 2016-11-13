package com.tomalancarroll.service;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.tomalancarroll.service.ContentfulConstants.DICTIONARY_FIELD_ID;

@Service
public class ContentfulService {
    private static final Logger logger = LoggerFactory.getLogger(ContentfulService.class);

    @Value("${contentful.api}")
    private String contentfulApi;

    @Autowired
    @Qualifier("contentfulDeliveryClient")
    private CDAClient contentfulDeliveryClient;

    @Autowired
    @Qualifier("contentfulPreviewClient")
    private CDAClient contentfulPreviewClient;

    @Autowired
    private ContentfulTypeIdResolver contentfulTypeIdResolver;

    @Cacheable("contentful")
    public String getTranslation(Locale locale, String subject) {
        CDAArray result = null;

        try {
            String translation_type_id = contentfulTypeIdResolver.getTranslationContentTypeId();
            if (usePreviewApi()) {
                result = contentfulPreviewClient.fetch(CDAEntry.class)
                        .where("content_type", translation_type_id)
                        .where("fields.subject", subject)
                        .where("limit", "1")
                        .all();

            } else {
                result = contentfulDeliveryClient.fetch(CDAEntry.class)
                        .where("content_type", translation_type_id)
                        .where("fields.subject", subject)
                        .where("limit", "1")
                        .all();
            }

            CDAEntry entry = (CDAEntry) result.items().get(0);
            Gson gson = new Gson();
            return gson.toJsonTree(entry.getField(DICTIONARY_FIELD_ID.getValue()))
                    .getAsJsonObject().toString();
        } catch (Exception e) {
            logger.error("Unable to get translation for subject " + subject +
                    " and locale " + locale, e);
            return null;
        }
    }

    private boolean usePreviewApi() {
        if ("preview".equals(contentfulApi)) {
            return true;
        } else {
            return false; // use delivery
        }
    }
}
