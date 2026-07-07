package com.zosoftware.solid.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String AUTH_FILE = "auth.properties";
    private static Properties cachedProperties;

    private AppConfig() {
    }

    public static String getString(Context context, String key, String defaultValue) {
        if (context == null || key == null) {
            return defaultValue;
        }
        String value = loadProperties(context).getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        String value = getString(context, key, "");
        if (value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Utils.loginfo("Invalid integer config for " + key);
            return defaultValue;
        }
    }

    private static synchronized Properties loadProperties(Context context) {
        if (cachedProperties != null) {
            return cachedProperties;
        }
        cachedProperties = new Properties();
        try (InputStream inputStream = context.getAssets().open(AUTH_FILE)) {
            cachedProperties.load(inputStream);
        } catch (IOException e) {
            Utils.loginfo("Missing local auth.properties configuration");
        }
        return cachedProperties;
    }
}
