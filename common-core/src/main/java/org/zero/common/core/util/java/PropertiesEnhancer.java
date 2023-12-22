package org.zero.common.core.util.java;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Zero
 */
@Slf4j
public class PropertiesEnhancer {
    private static final String XML = "xml";

    private final Properties properties;

    private PropertiesEnhancer(Properties properties) {
        this.properties = properties;
    }

    public static PropertiesEnhancer init() {
        return init(new Properties());
    }

    public static PropertiesEnhancer init(Properties properties) {
        return new PropertiesEnhancer(properties);
    }

    public static PropertiesEnhancer init(String filePath) {
        final Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            if (filePath.endsWith(XML)) {
                properties.loadFromXML(inputStream);
            } else {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            log.warn(String.format("load file[%s] failed", filePath), e);
        }
        return init(properties);
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public byte getByte(String key) {
        return Byte.parseByte(properties.getProperty(key));
    }

    public byte getByte(String key, byte defaultValue) {
        return Byte.parseByte(properties.getProperty(key, Byte.toString(defaultValue)));
    }

    public short getShort(String key) {
        return Short.parseShort(properties.getProperty(key));
    }

    public short getShort(String key, short defaultValue) {
        return Short.parseShort(properties.getProperty(key, Short.toString(defaultValue)));
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)));
    }

    public long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    public long getLong(String key, long defaultValue) {
        return Long.parseLong(properties.getProperty(key, Long.toString(defaultValue)));
    }

    public float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public float getFloat(String key, float defaultValue) {
        return Float.parseFloat(properties.getProperty(key, Float.toString(defaultValue)));
    }

    public double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    public double getDouble(String key, double defaultValue) {
        return Double.parseDouble(properties.getProperty(key, Double.toString(defaultValue)));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(defaultValue)));
    }
}

