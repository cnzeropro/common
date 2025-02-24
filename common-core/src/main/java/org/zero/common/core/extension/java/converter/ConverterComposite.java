package org.zero.common.core.extension.java.converter;

import org.zero.common.core.extension.java.TypeReference;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.reflect.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public class ConverterComposite {
    private static ConverterComposite instance;
    protected final Map<Type, Set<GenericConverter<?>>> converterMap = new HashMap<>();

    protected ConverterComposite() {
    }

    public static ConverterComposite getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ConverterComposite.class) {
                if (Objects.isNull(instance)) {
                    instance = new ConverterComposite();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        Collection<Class<?>> classes = ClassUtil.getClasses(ConverterComposite.class.getPackage());
        List<GenericConverter<?>> converters = classes.stream()
                .filter(GenericConverter.class::isAssignableFrom)
                .filter(clazz -> !clazz.isInterface())
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .map(clazz -> {
                    Optional<GenericConverter<?>> converterOpt = ReflectUtil.getFilteredFields(clazz, field -> Objects.equals(field.getType(), clazz))
                            .stream()
                            .findFirst()
                            .map(field -> ReflectUtil.getStaticFieldValue(field, GenericConverter.class));
                    if (converterOpt.isPresent()) {
                        return converterOpt.get();
                    }
                    Optional<Constructor<?>> constructorOpt = ReflectUtil.getConstructorOptByParam(clazz, ConverterComposite.class);
                    if (constructorOpt.isPresent()) {
                        return ReflectUtil.newInstance(clazz, this);
                    }
                    return ReflectUtil.newInstance(clazz);
                })
                .filter(GenericConverter.class::isInstance)
                .map(o -> ReflectUtil.<GenericConverter<?>>cast(o, new TypeReference<GenericConverter<?>>() {
                }))
                .collect(Collectors.toList());
        this.addConverter(converters);
    }

    public ConverterComposite addConverter(Collection<GenericConverter<?>> converters) {
        converters.forEach(this::addConverter);
        return this;
    }

    public ConverterComposite addConverter(GenericConverter<?> converter) {
        ReflectUtil.getMethodsByName(converter.getClass(), false, "convert")
                .stream()
                // 当子类继承带有泛型的父类或接口时，编译器会生成桥接方法来保持类型安全
                // 因此此处 convert 方法可能存在多个
                // 所以排除桥接方法（isBridge）和合成方法（isSynthetic）
                .filter(method -> !method.isBridge() && !method.isSynthetic())
                .findFirst()
                .ifPresent(method -> {
                    Type returnType = method.getGenericReturnType();
                    addConverter(returnType, converter);
                });
        return this;
    }

    public ConverterComposite addConverter(Type type, GenericConverter<?> converter) {
        converterMap.compute(type, (key, value) -> {
            Set<GenericConverter<?>> set = value;
            if (Objects.isNull(set)) {
                set = new TreeSet<>(Comparator.comparing(GenericConverter::order));
            }
            set.add(converter);
            return set;
        });
        return this;
    }

    public boolean canConvert(Type type, boolean exact) {
        if (exact) {
            return converterMap.containsKey(type);
        }
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return converterMap.keySet()
                    .stream()
                    .filter(Class.class::isInstance)
                    .map(Class.class::cast)
                    .anyMatch(c -> {
                        // 数组单独处理，如：int[] -> Integer[]
                        if (clazz.isArray() && c.isArray()) {
                            return ClassUtil.isAssignable(clazz.getComponentType(), c.getComponentType());
                        }
                        return ClassUtil.isAssignable(clazz, c);
                    });
        }
        return false;
    }

    public <T> T convertExactAndQuietly(Type type, Object source) {
        return convert(type, source, true, true);
    }

    public <T> T convertExact(Type type, Object source, boolean quietly) {
        return convert(type, source, true, quietly);
    }

    public <T> T convertQuietly(Type type, Object source, boolean exact) {
        return convert(type, source, exact, true);
    }

    public <T> T convert(Type type, Object source, boolean exact, boolean quietly) {
        Set<GenericConverter<?>> exactConverters = converterMap.getOrDefault(type, Collections.emptySet());
        Set<GenericConverter<?>> converters = new LinkedHashSet<>(exactConverters);
        if (!exact && type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            converterMap.keySet()
                    .stream()
                    .filter(Class.class::isInstance)
                    .map(Class.class::cast)
                    // 精确转换器已添加，此处过滤掉
                    .filter(c -> !c.equals(clazz))
                    .filter(c -> {
                        // 数组单独处理，如：int[] -> Integer[]
                        if (clazz.isArray() && c.isArray()) {
                            return ClassUtil.isAssignable(clazz.getComponentType(), c.getComponentType());
                        }
                        return ClassUtil.isAssignable(clazz, c);
                    })
                    .map(c -> converterMap.getOrDefault(c, Collections.emptySet()))
                    .forEach(converters::addAll);
        }
        for (GenericConverter<?> converter : converters) {
            try {
                @SuppressWarnings("unchecked")
                T converted = (T) converter.convert(source);
                return converted;
            } catch (Exception e) {
                if (!quietly) {
                    throw e;
                }
            }
        }
        // 找不到转换器时，直接强转并返回原对象
        try {
            @SuppressWarnings("unchecked")
            T casted = (T) source;
            return casted;
        } catch (Exception e) {
            if (!quietly) {
                throw e;
            }
            return null;
        }
    }

}
