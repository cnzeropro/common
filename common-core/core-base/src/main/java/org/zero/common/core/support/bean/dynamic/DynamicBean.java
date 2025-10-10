package org.zero.common.core.support.bean.dynamic;

import org.zero.common.core.extension.java.lang.reflect.GenericArrayTypeImpl;
import org.zero.common.core.extension.java.lang.reflect.ParameterizedTypeImpl;
import org.zero.common.core.support.converter.ConverterComposite;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.util.MapUtil;
import org.zero.common.data.constant.ConstantPool;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2020/12/30
 */
public class DynamicBean extends LinkedHashMap<CharSequence, Object> {
    /* ***************************************************** creater ***************************************************** */
    public DynamicBean() {
        this(MapUtil.DEFAULT_CAPACITY);
    }

    protected DynamicBean(int size) {
        super(MapUtil.calculateCapacity(size));
    }

    public static DynamicBean create() {
        return new DynamicBean();
    }

    public static DynamicBean create(int numMappings) {
        return new DynamicBean(numMappings);
    }

    public static DynamicBean create(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        DynamicBean instance = new DynamicBean(fields.length);
        for (Field field : fields) {
            instance.set(field.getName(), FieldUtil.getValue(field, bean));
        }
        return instance;
    }

    public static DynamicBean create(Class<?> beanType) {
        Field[] fields = beanType.getDeclaredFields();
        DynamicBean instance = new DynamicBean(fields.length);
        for (Field field : fields) {
            instance.set(field.getName(), ClassUtil.getDefaultValue(field.getType()));
        }
        return instance;
    }

    public static DynamicBean create(Map<? extends CharSequence, ?> map) {
        return new DynamicBean(map.size()).set(map);
    }


    /* ***************************************************** getter ***************************************************** */
    public Object get(CharSequence name) {
        return super.get(name);
    }

    public Optional<Object> getOpt(CharSequence name) {
        return Optional.ofNullable(this.get(name));
    }

    public <T> Optional<T> getOpt(CharSequence name, Type type) {
        return this.getOpt(name)
                .filter(o -> converterComposite.canConvert(type, false))
                .map(o -> converterComposite.convertQuietly(type, o, false));
    }

    public <T> T get(CharSequence name, Type type, T defaultValue) {
        return this.<T>getOpt(name, type).orElse(defaultValue);
    }

    public <T> T get(CharSequence name, Type type) {
        return get(name, type, null);
    }

    public byte getByte(CharSequence name) {
        return get(name, Byte.class, ConstantPool.BYTE_ZERO);
    }

    public short getShort(CharSequence name) {
        return get(name, Short.class, ConstantPool.SHORT_ZERO);
    }

    public int getInt(CharSequence name) {
        return get(name, Integer.class, ConstantPool.INT_ZERO);
    }

    public long getLong(CharSequence name) {
        return get(name, Long.class, ConstantPool.LONG_ZERO);
    }

    public float getFloat(CharSequence name) {
        return get(name, Float.class, ConstantPool.FLOAT_ZERO);
    }

    public double getDouble(CharSequence name) {
        return get(name, Double.class, ConstantPool.DOUBLE_ZERO);
    }

    public char getChar(CharSequence name) {
        return get(name, Character.class, ConstantPool.CHAR_ZERO);
    }

    public boolean getBoolean(CharSequence name) {
        return get(name, Boolean.class, ConstantPool.BOOLEAN_FALSE);
    }

    public String getString(CharSequence name) {
        return getString(name, null);
    }

    public String getString(CharSequence name, String defaultValue) {
        return get(name, String.class, defaultValue);
    }

    public <T> Collection<T> getCollection(CharSequence name, Type type) {
        return this.get(name, ParameterizedTypeImpl.make(Collection.class, type));
    }

    public <T> T[] getArray(CharSequence name, Type type) {
        return this.get(name, GenericArrayTypeImpl.make(type));
    }

    public <T> T getAndConvert(CharSequence name, Function<Object, T> converter) {
        Object value = get(name);
        return converter.apply(value);
    }

    /* ***************************************************** setter ***************************************************** */
    public DynamicBean set(CharSequence name, Object value, boolean replace) {
        if (replace) {
            put(name, value);
        } else {
            putIfAbsent(name, value);
        }
        return this;
    }

    public DynamicBean set(CharSequence name, Object value) {
        put(name, value);
        return this;
    }

    public DynamicBean setIfAbsent(CharSequence name, Object value) {
        putIfAbsent(name, value);
        return this;
    }

    public DynamicBean computeAndSet(CharSequence name, BiFunction<? super CharSequence, Object, Object> remappingFunction) {
        compute(name, remappingFunction);
        return this;
    }

    public DynamicBean computeAndSetIfAbsent(CharSequence name, Function<? super CharSequence, Object> mappingFunction) {
        computeIfAbsent(name, mappingFunction);
        return this;
    }

    public DynamicBean computeAndSetIfPresent(CharSequence name, BiFunction<? super CharSequence, Object, Object> remappingFunction) {
        computeIfPresent(name, remappingFunction);
        return this;
    }

    public DynamicBean set(Map<? extends CharSequence, ?> map) {
        putAll(map);
        return this;
    }

    /* ***************************************************** other ***************************************************** */
    protected static ConverterComposite converterComposite = ConverterComposite.getInstance();

    public static void setConverter(ConverterComposite converterComposite) {
        DynamicBean.converterComposite = converterComposite;
    }
}
