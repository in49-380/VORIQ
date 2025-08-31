package com.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationReader {

    private ConfigurationReader() {
    }

    private static Properties properties;

    static {
        try {
            String path = "src/resources/configuration.properties";
            FileInputStream inputStream = new FileInputStream(path);

            properties = new Properties();
            properties.load(inputStream);

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String keyName) {
        return properties.getProperty(keyName);
    }
}
