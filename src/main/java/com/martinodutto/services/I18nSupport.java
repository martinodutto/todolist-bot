package com.martinodutto.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contains static methods useful to translate the externalized strings.
 */
public class I18nSupport {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("Language", Locale.US);

    /**
     * Gets a translation for the passed externalized string.
     *
     * @param key Key of the externalized string.
     * @return Translation for the string.
     */
    public static String i18nize(@NotNull @PropertyKey(resourceBundle = "Language") String key) {
        return bundle.getString(key);
    }

    /**
     * Gets a translation for the passed externalized string and the given parameters.
     *
     * @param key    Key of the externalized string.
     * @param params Parameters to be substituted into the resulting string.
     * @return Translation for the string, with the given parameters.
     */
    @NotNull
    public static String i18nize(@NotNull @PropertyKey(resourceBundle = "Language") String key, @Nullable Object... params) {
        String value = bundle.getString(key);

        if (params != null && params.length > 0) return MessageFormat.format(value, params);

        return value;
    }
}
