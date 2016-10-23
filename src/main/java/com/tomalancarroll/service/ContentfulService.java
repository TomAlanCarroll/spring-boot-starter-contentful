package com.tomalancarroll.service;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.tomalancarroll.service.ContentfulConstants.*;

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

    public String getTranslation(String subject, Locale locale) {
        CDAArray result = null;

        try {
            if (usePreviewApi()) {
                result = contentfulPreviewClient.fetch(CDAEntry.class)
                        .where("content_type", TRANSLATION_CONTENT_TYPE_ID.getValue())
                        //.where("fields.subject", subject)
                        //.where("limit", "1")
                        .all();

            } else {
                result = contentfulDeliveryClient.fetch(CDAEntry.class)
                        .where("content_type", TRANSLATION_CONTENT_TYPE_ID.getValue())
                        //.where("fields.subject", subject)
                        //.where("limit", "1")
                        .all();
            }

            return result.items().get(0).getAttribute(DICTIONARY_FIELD_ID.getValue());
        } catch (Exception e) {
            logger.error("Unable to get translation for subject " + subject +
                    " and locale " + locale, e);
            return (new JsonObject()).toString();
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
