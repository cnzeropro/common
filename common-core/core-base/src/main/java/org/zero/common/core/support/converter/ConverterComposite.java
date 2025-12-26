package org.zero.common.core.support.converter;

import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.reflect.ConstructorUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;
import org.zero.common.data.model.util.Ordered;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public class ConverterComposite {
    private static ConverterComposite instance;
    protected final Cache<Type, Set<ObjectConverter<?>>> converters = MapCache.of(HashMap::new);

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
        List<ObjectConverter<?>> converters = classes.stream()
                .filter(ObjectConverter.class::isAssignableFrom)
                .filter(clazz -> !clazz.isInterface())
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .map(clazz -> {
                    Optional<ObjectConverter<?>> converterOpt = FieldUtil.listStatic(clazz)
                            .stream()
                            .filter(field -> ClassUtil.isAssignable(field.getType(), clazz))
                            .findFirst()
                            .map(field -> FieldUtil.getStaticValue(field, ObjectConverter.class));
                    if (converterOpt.isPresent()) {
                        return converterOpt.get();
                    }
                    Optional<Constructor<?>> constructorOpt = ConstructorUtil.getOptByParam(clazz, ConverterComposite.class);
                    if (constructorOpt.isPresent()) {
                        return ConstructorUtil.newInstance(constructorOpt.get(), this);
                    }
                    return ConstructorUtil.newInstance(clazz);
                })
                .filter(ObjectConverter.class::isInstance)
                .map(o -> ClassUtil.<ObjectConverter<?>>cast(o, new TypeReference<ObjectConverter<?>>() {
                }))
                .collect(Collectors.toList());
        this.addConverter(converters);
    }

    public ConverterComposite addConverter(Collection<ObjectConverter<?>> converters) {
        converters.forEach(this::addConverter);
        return this;
    }

    public ConverterComposite addConverter(ObjectConverter<?> converter) {
        MethodUtil.listByName(converter.getClass(), false, "convert")
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

    public ConverterComposite addConverter(Type type, ObjectConverter<?> converter) {
        converters.compute(type, (key, value) -> {
            Set<ObjectConverter<?>> set = value;
            if (Objects.isNull(set)) {
                set = new HashSet<>();
            }
            set.add(converter);
            return set;
        });
        return this;
    }

    public Collection<ObjectConverter<?>> getConverters(Type type, boolean exact) {
        Set<ObjectConverter<?>> exactConverters = converters.getOrDefault(type, Collections.emptySet());
        Set<ObjectConverter<?>> converters = new HashSet<>(exactConverters);
        if (!exact) {
            Set<Type> types = this.converters.asMap().keySet();
            types.stream()
                    // 精确类型转换器已添加，此处排除
                    .filter(t -> !t.equals(type))
                    .filter(t -> ClassUtil.isConvertible(type, t))
                    .map(c -> this.converters.getOrDefault(c, Collections.emptySet()))
                    .forEach(converters::addAll);
        }
        return converters.stream().sorted(Comparator.comparingInt(Ordered::order)).collect(Collectors.toList());
    }

    public boolean canConvert(Type type, boolean exact) {
        return !this.getConverters(type, exact).isEmpty();
    }

    public <T> T convertExactAndQuietly(Type type, Object source) {
        return this.convert(type, source, true, true);
    }

    public <T> T convertExact(Type type, Object source, boolean quietly) {
        return this.convert(type, source, true, quietly);
    }

    public <T> T convertQuietly(Type type, Object source, boolean exact) {
        return this.convert(type, source, exact, true);
    }

    public <T> T convert(Type type, Object source, boolean exact, boolean quietly) {
        Collection<ObjectConverter<?>> converters = this.getConverters(type, exact);
        for (ObjectConverter<?> converter : converters) {
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
            return ClassUtil.cast(source, type);
        } catch (Exception e) {
            if (!quietly) {
                throw e;
            }
            return null;
        }
    }

}
