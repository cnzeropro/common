package org.zero.common.core.extension.java.converter;

import org.zero.common.core.extension.java.TypeReference;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.reflect.ReflectUtil;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    protected static final Map<Type, Set<GenericConverter<?>>> CONVERTER_MAP = new HashMap<>();

    protected ConverterComposite() {
    }

    public static ConverterComposite getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ConverterComposite.class) {
                if (Objects.isNull(instance)) {
                    instance = new ConverterComposite();
                    init();
                }
            }
        }
        return instance;
    }

    private static void init() {
        Collection<Class<?>> classes = ClassUtil.getClasses(ConverterComposite.class.getPackage());
        List<GenericConverter<?>> converters = classes.stream()
                .filter(GenericConverter.class::isAssignableFrom)
                .map(clazz -> {
                    Optional<GenericConverter<?>> converterOpt = ReflectUtil.getFilteredFields(clazz, field -> Objects.equals(field.getType(), clazz))
                            .stream()
                            .findFirst()
                            .map(field -> ReflectUtil.getStaticFieldValue(field, GenericConverter.class));
                    if (converterOpt.isPresent()) {
                        return converterOpt.get();
                    }
                    return ReflectUtil.newInstance(clazz);
                })
                .filter(GenericConverter.class::isInstance)
                .map(o -> ReflectUtil.<GenericConverter<?>>casting(o, new TypeReference<GenericConverter<?>>() {
                }))
                .collect(Collectors.toList());
        addConverter(converters);
    }

    public static void addConverter(Collection<GenericConverter<?>> converters) {
        converters.forEach(ConverterComposite::addConverter);
    }

    public static void addConverter(GenericConverter<?> converter) {
        ReflectUtil.getMethodsByName(converter.getClass(), "convert")
                .stream()
                .findFirst()
                .ifPresent(method -> {
                    Type returnType = method.getGenericReturnType();
                    CONVERTER_MAP.compute(returnType, (key, value) -> {
                        Set<GenericConverter<?>> set = value;
                        if (Objects.isNull(set)) {
                            set = new TreeSet<>(Comparator.comparing(GenericConverter::order));
                        }
                        set.add(converter);
                        return set;
                    });
                });
    }

    public boolean canConvert(Type type) {
        return CONVERTER_MAP.containsKey(type);
    }

    public <T> T convert(Type type, Object source) {
        Set<GenericConverter<?>> converters = CONVERTER_MAP.getOrDefault(type, Collections.emptySet());
        for (GenericConverter<?> converter : converters) {
            try {
                @SuppressWarnings("unchecked")
                T converted = (T) converter.convert(source);
                return converted;
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
