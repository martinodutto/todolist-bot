package com.martinodutto.services;

import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class I18nSupport {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("Language");

    public static String i18nize(@PropertyKey(resourceBundle = "Language") String key) {
        return bundle.getString(key);
    }

    public static String i18nize(@PropertyKey(resourceBundle = "Language") String key, Object... params) {
        String value = bundle.getString(key);

        if (params != null && params.length > 0) return MessageFormat.format(value, params);

        return value;
    }
}
