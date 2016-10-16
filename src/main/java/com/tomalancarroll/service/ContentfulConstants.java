package com.tomalancarroll.service;

public enum ContentfulConstants {
    TRANSLATION_CONTENT_TYPE_NAME("Translation");

    private final String value;

    ContentfulConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
