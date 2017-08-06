package com.martinodutto.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class PropertiesManager {

    private static final String PROPERTIES_FILE_NAME = "bot.properties";

    private final Properties properties = new Properties();

    public PropertiesManager() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream(File.separator + PROPERTIES_FILE_NAME)) {
            properties.load(is);
        }
    }

    public String getProperty(@NotNull String name) {
        return properties.getProperty(name);
    }
}
