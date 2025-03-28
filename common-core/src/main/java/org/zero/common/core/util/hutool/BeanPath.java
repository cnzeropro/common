package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/18
 */
@RequiredArgsConstructor(staticName = "of")
public class BeanPath {
    protected final List<Serializable> nodes;

    /**
     * 解析
     * <p>
     * 支持：
     * <ul>
     *     <li>a.b.c</li>
     *     <li>[0].b.c['0']</li>
     *     <li>a[0][1][2].b</li>
     *     <li>[3].   .a[0][1][2].b['2025'].c[fff].d[5]['2'][0].e</li>
     *     <li>a.b[0].c[d]</li>
     *     <li>...</li>
     * </ul>
     *
     * @param expression Bean Path 表达式
     * @return {@link BeanPath}
     */
    public static BeanPath parse(final CharSequence expression) {
        List<Serializable> nodes = CharSequenceUtil.split(expression, '.')
                .stream()
                .flatMap(patternPart -> {
                    // like a[0]
                    if (CharSequenceUtil.containsAll(patternPart, "[", "]")) {
                        List<Serializable> subNodes = ListUtil.list(false);
                        String propertyName = CharSequenceUtil.subBefore(patternPart, "[", false);
                        if (CharSequenceUtil.isNotBlank(propertyName)) {
                            subNodes.add(propertyName);
                        }
                        // like a[5][2][0]
                        String[] subNames = CharSequenceUtil.subBetweenAll(patternPart, "[", "]");
                        for (String subName : subNames) {
                            if (NumberUtil.isNumber(subName)) {
                                int index = NumberUtil.parseInt(subName);
                                subNodes.add(index);
                            } else {
                                if (CharSequenceUtil.isWrap(subName, "'")) {
                                    String subPropertyName = CharSequenceUtil.unWrap(subName, '\'');
                                    subNodes.add(subPropertyName);
                                } else if (CharSequenceUtil.isWrap(subName, "\"")) {
                                    String subPropertyName = CharSequenceUtil.unWrap(subName, '"');
                                    subNodes.add(subPropertyName);
                                } else {
                                    subNodes.add(subName);
                                }
                            }
                        }
                        return subNodes.stream();
                    }
                    // like a
                    return Stream.of(patternPart);
                })
                .collect(Collectors.toList());
        return of(nodes);
    }

    public Optional<Object> getOpt(final Object bean) {
        return Optional.ofNullable(this.get(bean));
    }

    public Object get(final Object bean) {
        return this.get(bean, nodes);
    }

    protected Object get(final Object bean, final List<Serializable> nodes) {
        Object obj = bean;
        for (Serializable node : nodes) {
            obj = this.get(obj, node);
            if (Objects.isNull(obj)) {
                return null;
            }
        }
        return obj;
    }

    protected Type getType(final Object bean, final List<Serializable> nodes) {
        Type type = bean.getClass();
        for (Serializable node : nodes) {
            Type currentType = this.getType(type, node);
            if (Objects.isNull(currentType)) {
                return null;
            }
            type = currentType;
        }
        return type;
    }

    protected Type getType(final Type parentType, final Serializable node) {
        if (node instanceof Integer) {
            if (parentType instanceof Class) {
                Class<?> parentClass = (Class<?>) parentType;
                if (parentClass.isArray()) {
                    return parentClass.getComponentType();
                }
            }
            if (parentType instanceof ParameterizedType) {
                return TypeUtil.getTypeArgument(parentType, 0);
            }
            if (parentType instanceof GenericArrayType) {
                return ((GenericArrayType) parentType).getGenericComponentType();
            }
        }
        if (node instanceof String) {
            if (parentType instanceof Class) {
                Class<?> parentClass = (Class<?>) parentType;
                if (Map.class.isAssignableFrom(parentClass)) {
                    return TypeUtil.getTypeArgument(parentClass, 1);
                }
                Field field = ReflectUtil.getField(parentClass, (String) node);
                return TypeUtil.getType(field);
            }
            if (parentType instanceof ParameterizedType) {
                return TypeUtil.getTypeArgument(parentType, 1);
            }
        }
        return null;
    }

    protected Object get(final Object obj, final Serializable node) {
        if (Objects.isNull(obj)) {
            return null;
        }
        // 数组/集合访问
        if (node instanceof Integer) {
            int index = (int) node;
            if (obj instanceof Collection) {
                return CollUtil.get((Collection<?>) obj, index);
            }
            if (obj instanceof Iterator) {
                return IterUtil.get((Iterator<?>) obj, index);
            }
            if (obj instanceof Iterable) {
                return IterUtil.get(((Iterable<?>) obj).iterator(), index);
            }
            if (obj instanceof Enumeration) {
                return IterUtil.get(IterUtil.asIterator((Enumeration<?>) obj), index);
            }
            if (ArrayUtil.isArray(obj)) {
                return ArrayUtil.get(obj, index);
            }
        }
        // 属性/键值访问
        if (node instanceof String) {
            String name = (String) node;
            if (obj instanceof Map) {
                return ((Map<?, ?>) obj).get(name);
            }
            try {
                return ReflectUtil.getFieldValue(obj, name);
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 设置值
     *
     * @param bean  Bean 对象
     * @param value 值
     * @return 是否设置成功
     */
    public boolean set(final Object bean, final Object value) {
        Object parentObj;
        Object valueObj = value;
        int size = nodes.size();
        for (int i = 1; i <= size; i++) {
            List<Serializable> parentNodes = nodes.subList(0, size - i);
            Serializable currentNode = nodes.get(size - i);
            parentObj = this.get(bean, parentNodes);
            if (Objects.isNull(parentObj)) {
                Type type = this.getType(bean, parentNodes);
                if (Objects.isNull(type)) {
                    if (currentNode instanceof Integer) {
                        parentObj = ListUtil.list(false);
                    } else if (currentNode instanceof String) {
                        parentObj = MapUtil.newHashMap(true);
                    } else {
                        // never come here
                        return false;
                    }
                } else {
                    Class<?> clazz = TypeUtil.getClass(type);
                    parentObj = ReflectUtil.newInstanceIfPossible(clazz);
                }
            }
            if (!this.set(parentObj, valueObj, currentNode)) {
                return false;
            }
            valueObj = parentObj;
        }
        return true;
    }

    protected boolean set(final Object obj, final Object value, final Serializable node) {
        if (node instanceof Integer) {
            int index = (int) node;
            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) obj;
                ListUtil.setOrPadding(list, index, value);
                return true;
            }
            if (ArrayUtil.isArray(obj)) {
                if (index < Array.getLength(obj)) {
                    Array.set(obj, index, value);
                    return true;
                }
                return false;
            }
        }
        if (node instanceof String) {
            String name = (String) node;
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) obj;
                map.put(name, value);
                return true;
            }
            try {
                ReflectUtil.setFieldValue(obj, name, value);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
        return false;
    }
}
