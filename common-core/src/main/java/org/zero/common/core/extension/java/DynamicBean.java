package org.zero.common.core.extension.java;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2020/12/30
 */
public class DynamicBean extends LinkedHashMap<CharSequence, Object> {
    /* ***************************************************** creater ***************************************************** */
    public DynamicBean() {
        super(16);
    }

    public static DynamicBean create() {
        return new DynamicBean();
    }

    public static DynamicBean create(Class<?> beanType) {
        DynamicBean instance = new DynamicBean();
        for (Field field : beanType.getDeclaredFields()) {
            instance.set(field.getName(), null);
        }
        return instance;
    }

    public static DynamicBean create(Map<? extends CharSequence, ?> map) {
        return new DynamicBean().set(map);
    }

    /* ***************************************************** getter ***************************************************** */
    public <T> Optional<T> getOpt(CharSequence name, Class<T> asType) {
        return Optional.ofNullable(get(name)).map(asType::cast);
    }

    public <T> T get(CharSequence name, Class<T> asType, T defaultValue) {
        return getOpt(name, asType).orElse(defaultValue);
    }

    public <T> T get(CharSequence name, Class<T> asType) {
        return get(name, asType, null);
    }

    public byte getByte(CharSequence name) {
        return get(name, Byte.class, (byte) 0);
    }

    public short getShort(CharSequence name) {
        return get(name, Short.class, (short) 0);
    }

    public int getInt(CharSequence name) {
        return get(name, Integer.class, 0);
    }

    public long getLong(CharSequence name) {
        return get(name, Long.class, 0L);
    }

    public float getFloat(CharSequence name) {
        return get(name, Float.class, 0.0F);
    }

    public double getDouble(CharSequence name) {
        return get(name, Double.class, 0.0D);
    }

    public char getChar(CharSequence name) {
        return get(name, Character.class, '\u0000');
    }

    public boolean getBoolean(CharSequence name) {
        return get(name, Boolean.class, false);
    }

    /* ***************************************************** setter ***************************************************** */
    public DynamicBean set(CharSequence name, Object value) {
        put(name, value);
        return this;
    }

    public DynamicBean set(Map<? extends CharSequence, ?> map) {
        putAll(map);
        return this;
    }
}
