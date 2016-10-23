package com.tomalancarroll.service;

public enum ContentfulConstants {
    TRANSLATION_CONTENT_TYPE_NAME("Translation"),
    TRANSLATION_CONTENT_TYPE_ID("translation"),
    SUBJECT_FIELD_ID("subject"),
    SUBJECT_FIELD_NAME("Subject"),
    DICTIONARY_FIELD_ID("dictionary"),
    DICTIONARY_FIELD_NAME("Dictionary");

    private final String value;

    ContentfulConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
