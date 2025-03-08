package org.zero.common.core.extension.java;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zero
 */
@Slf4j
public class PropertiesEnhancer {
    public static PropertiesEnhancer init() {
        return init(new Properties());
    }

    public static PropertiesEnhancer init(Properties properties) {
        return new PropertiesEnhancer(properties);
    }

    @SneakyThrows
    public static PropertiesEnhancer init(String filePath) {
        final Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            if (filePath.endsWith(XML)) {
                properties.loadFromXML(inputStream);
            } else {
                properties.load(inputStream);
            }
        }
        return init(properties);
    }

    public Optional<Byte> getByteOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Byte::parseByte);
    }

    public Byte getBoxedByte(String key) {
        return this.getByteOpt(key).orElse(null);
    }

    public Byte getBoxedByte(String key, Byte defaultValue) {
        return this.getByteOpt(key).orElse(defaultValue);
    }

    public byte getByte(String key) {
        return this.getBoxedByte(key, (byte) 0);
    }

    public byte getByte(String key, byte defaultValue) {
        return this.getBoxedByte(key, defaultValue);
    }

    public Optional<Short> getShortOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Short::parseShort);
    }

    public Short getBoxedShort(String key) {
        return this.getShortOpt(key).orElse(null);
    }

    public Short getBoxedShort(String key, Short defaultValue) {
        return this.getShortOpt(key).orElse(defaultValue);
    }

    public short getShort(String key) {
        return this.getShortOpt(key).orElse((short) 0);
    }

    public short getShort(String key, short defaultValue) {
        return this.getShortOpt(key).orElse(defaultValue);
    }

    public Optional<Integer> getIntOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Integer::parseInt);
    }

    public Integer getBoxedInt(String key) {
        return this.getIntOpt(key).orElse(null);
    }

    public Integer getBoxedInt(String key, Integer defaultValue) {
        return this.getIntOpt(key).orElse(defaultValue);
    }

    public int getInt(String key) {
        return this.getIntOpt(key).orElse(0);
    }

    public int getInt(String key, int defaultValue) {
        return this.getIntOpt(key).orElse(defaultValue);
    }

    public Optional<Long> getLongOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Long::parseLong);
    }

    public Long getBoxedLong(String key) {
        return this.getLongOpt(key).orElse(null);
    }

    public Long getBoxedLong(String key, Long defaultValue) {
        return this.getLongOpt(key).orElse(defaultValue);
    }

    public long getLong(String key) {
        return this.getLongOpt(key).orElse(0L);
    }

    public long getLong(String key, long defaultValue) {
        return this.getLongOpt(key).orElse(defaultValue);
    }

    public Optional<Float> getFloatOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Float::parseFloat);
    }

    public Float getBoxedFloat(String key) {
        return this.getFloatOpt(key).orElse(null);
    }

    public Float getBoxedFloat(String key, Float defaultValue) {
        return this.getFloatOpt(key).orElse(defaultValue);
    }

    public float getFloat(String key) {
        return this.getFloatOpt(key).orElse(0.0f);
    }

    public float getFloat(String key, float defaultValue) {
        return this.getFloatOpt(key).orElse(defaultValue);
    }

    public Optional<Double> getDoubleOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Double::parseDouble);
    }

    public Double getBoxedDouble(String key) {
        return this.getDoubleOpt(key).orElse(null);
    }

    public Double getBoxedDouble(String key, Double defaultValue) {
        return this.getDoubleOpt(key).orElse(defaultValue);
    }

    public double getDouble(String key) {
        return this.getDoubleOpt(key).orElse(0.0);
    }

    public double getDouble(String key, double defaultValue) {
        return this.getDoubleOpt(key).orElse(defaultValue);
    }

    public Optional<Boolean> getBooleanOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(Boolean::parseBoolean);
    }

    public Boolean getBoxedBoolean(String key) {
        return this.getBooleanOpt(key).orElse(null);
    }

    public Boolean getBoxedBoolean(String key, Boolean defaultValue) {
        return this.getBooleanOpt(key).orElse(defaultValue);
    }

    public boolean getBoolean(String key) {
        return this.getBooleanOpt(key).orElse(Boolean.FALSE);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getBooleanOpt(key).orElse(defaultValue);
    }

    public Optional<BigDecimal> getBigDecimalOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(BigDecimal::new);
    }

    public BigDecimal getBigDecimal(String key) {
        return this.getBigDecimalOpt(key).orElse(null);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return this.getBigDecimalOpt(key).orElse(defaultValue);
    }

    public Optional<BigInteger> getBigIntegerOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(BigInteger::new);
    }

    public BigInteger getBigInteger(String key) {
        return this.getBigIntegerOpt(key).orElse(null);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return this.getBigIntegerOpt(key).orElse(defaultValue);
    }

    public Optional<Class<?>> getClassOpt(String key) {
        return Optional.ofNullable(this.getProperty(key))
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException ignored) {
                        return null;
                    }
                });
    }

    public Class<?> getClass(String key) {
        return this.getClassOpt(key).orElse(null);
    }

    public Class<?> getClass(String key, Class<?> defaultValue) {
        return this.getClassOpt(key).orElse(defaultValue);
    }

    private static final String XML = "xml";

    @Delegate(types = IncludedDelegates.class)
    private final Properties properties;

    private PropertiesEnhancer(Properties properties) {
        this.properties = properties;
    }

    private interface IncludedDelegates {
        void load(Reader reader) throws IOException;

        void load(InputStream inStream) throws IOException;

        void store(Writer writer, String comments) throws IOException;

        void store(OutputStream out, String comments) throws IOException;

        void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException;

        void storeToXML(OutputStream os, String comment) throws IOException;

        void storeToXML(OutputStream os, String comment, String encoding) throws IOException;

        Object setProperty(String key, String value);

        String getProperty(String key);

        String getProperty(String key, String defaultValue);

        Set<String> stringPropertyNames();

        void list(PrintStream out);

        void list(PrintWriter out);
    }
}

