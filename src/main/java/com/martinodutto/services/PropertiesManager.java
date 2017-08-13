package com.martinodutto.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class PropertiesManager {

    private final String propertiesFileName;

    private final Properties properties = new Properties();

    @Autowired
    public PropertiesManager(@Value("${properties.file.name}") String propertiesFileName) throws IOException {
        this.propertiesFileName = propertiesFileName;
        try (InputStream is = this.getClass().getResourceAsStream(File.separator + this.propertiesFileName)) {
            properties.load(is);
        }
    }

    /**
     * Gets the value of the property that corresponds to the passed key.
     *
     * @param name Key of the property.
     * @return Value corresponding to the key.
     */
    public String getProperty(@NotNull String name) {
        return properties.getProperty(name);
    }
}
