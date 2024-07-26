package org.zero.common.core.util.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.zero.common.core.util.java.reflect.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zero
 * @since 2024/7/15
 */
public class QueryWrapperUtil {
    public static final String SELECT_ALL = "*";

    protected static ConcurrentMap<Class<?>, Map<String, Collection<Method>>> METHOD_CACHE = new ConcurrentHashMap<>();

    public static <E> QueryWrapper<E> setSelect(QueryWrapper<E> queryWrapper, String[] columns) {
        return setSelect(queryWrapper, CollectionUtils.toList(columns));
    }

    public static <E> QueryWrapper<E> setSelect(QueryWrapper<E> queryWrapper, Collection<String> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return queryWrapper;
        }
        columns.forEach(column -> queryWrapper.select(StringUtils.isNotBlank(column), column));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setSelect(QueryWrapper<E> queryWrapper, Class<E> clazz, String[] properties) {
        return setSelect(queryWrapper, clazz, CollectionUtils.toList(properties));
    }

    public static <E> QueryWrapper<E> setSelect(QueryWrapper<E> queryWrapper, Class<E> clazz, Collection<String> properties) {
        List<String> columns;
        if (isSelectAll(properties)) {
            columns = getColumns(clazz);
        } else {
            columns = toColumns(clazz, properties);
        }
        return setSelect(queryWrapper, columns);
    }

    public static <E> QueryWrapper<E> setIsNull(QueryWrapper<E> queryWrapper, String[] columns) {
        return setIsNull(queryWrapper, CollectionUtils.toList(columns));
    }

    public static <E> QueryWrapper<E> setIsNull(QueryWrapper<E> queryWrapper, Collection<String> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return queryWrapper;
        }
        columns.forEach(column -> queryWrapper.isNull(StringUtils.isNotBlank(column), column));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setIsNull(QueryWrapper<E> queryWrapper, Class<E> clazz, String[] properties) {
        return setIsNull(queryWrapper, clazz, CollectionUtils.toList(properties));
    }

    public static <E> QueryWrapper<E> setIsNull(QueryWrapper<E> queryWrapper, Class<E> clazz, Collection<String> properties) {
        List<String> columns = toColumns(clazz, properties);
        return setIsNull(queryWrapper, columns);
    }

    public static <E> QueryWrapper<E> setIsNotNull(QueryWrapper<E> queryWrapper, String[] columns) {
        return setIsNotNull(queryWrapper, CollectionUtils.toList(columns));
    }

    public static <E> QueryWrapper<E> setIsNotNull(QueryWrapper<E> queryWrapper, Collection<String> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return queryWrapper;
        }
        columns.forEach(column -> queryWrapper.isNotNull(StringUtils.isNotBlank(column), column));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setIsNotNull(QueryWrapper<E> queryWrapper, Class<E> clazz, String[] properties) {
        return setIsNotNull(queryWrapper, clazz, CollectionUtils.toList(properties));
    }

    public static <E> QueryWrapper<E> setIsNotNull(QueryWrapper<E> queryWrapper, Class<E> clazz, Collection<String> properties) {
        List<String> columns = toColumns(clazz, properties);
        return setIsNotNull(queryWrapper, columns);
    }

    public static <E> QueryWrapper<E> setEq(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.eq(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setEq(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.eq(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setNe(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ne(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setNe(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ne(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setGt(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.gt(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setGt(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.gt(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setGe(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ge(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setGe(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ge(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setLt(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.lt(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setLt(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.lt(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setLe(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.le(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setLe(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.le(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.like(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.like(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setNotLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLike(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setNotLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLike(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setLeftLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeLeft(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setLeftLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeLeft(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setNotLeftLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeLeft(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setNotLeftLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeLeft(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setRightLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeRight(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setRightLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeRight(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E> QueryWrapper<E> setNotRightLike(QueryWrapper<E> queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeRight(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper<E> setNotRightLike(QueryWrapper<E> queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeRight(Objects.nonNull(value), name, value));
        return queryWrapper;
    }

    public static <T> QueryWrapper<T> setGroupBy(QueryWrapper<T> queryWrapper, String[] columns) {
        return setGroupBy(queryWrapper, CollectionUtils.toList(columns));
    }

    public static <T> QueryWrapper<T> setGroupBy(QueryWrapper<T> queryWrapper, Collection<String> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return queryWrapper;
        }
        columns.forEach(column -> queryWrapper.groupBy(StringUtils.isNotBlank(column), column));
        return queryWrapper;
    }

    public static <T> QueryWrapper<T> setGroupBy(QueryWrapper<T> queryWrapper, Class<T> clazz, String[] properties) {
        return setGroupBy(queryWrapper, clazz, CollectionUtils.toList(properties));
    }

    public static <T> QueryWrapper<T> setGroupBy(QueryWrapper<T> queryWrapper, Class<T> clazz, Collection<String> properties) {
        List<String> columns = toColumns(clazz, properties);
        return setGroupBy(queryWrapper, columns);
    }

    /**
     * 判断是否选择了所有字段
     */
    protected static boolean isSelectAll(final String[] strings) {
        return isSelectAll(CollectionUtils.toList(strings));
    }

    /**
     * 判断是否选择了所有字段
     */
    protected static boolean isSelectAll(final Collection<String> strings) {
        if (CollectionUtils.isEmpty(strings)) {
            return false;
        }
        return strings.stream().anyMatch(QueryWrapperUtil::isSelectAll);
    }

    /**
     * 判断是否选择了所有字段
     */
    protected static boolean isSelectAll(final String str) {
        return SELECT_ALL.equals(str);
    }

    /**
     * 属性名（实体-驼峰命名法，如：createTime）转化为字段名（数据库-蛇形命名法，如：create_time）
     * <p>
     * 注意：此方法没有用到 MP 属性名与字段名的关联关系，只是单纯将驼峰命名法转化为蛇形命名法
     */
    protected static String toColumn(final String property) {
        return StringUtils.camelToUnderline(property);
    }

    /**
     * 属性名（实体-驼峰命名法，如：createTime）转化为字段名（数据库-蛇形命名法，如：create_time）
     * <p>
     * 注意：此方法没有用到 MP 属性名与字段名的关联关系，只是单纯将驼峰命名法转化为蛇形命名法
     */
    protected static List<String> toColumns(final Collection<String> properties) {
        return properties.stream()
                .map(QueryWrapperUtil::toColumn)
                .collect(Collectors.toList());
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static String toColumn(final Class<?> clazz, final String property) {
        return toColumnOpt(clazz, property).orElse(null);
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static Optional<String> toColumnOpt(final Class<?> clazz, final String property) {
        return getTableFieldInfos(clazz).stream()
                // 保证实体存在该属性（有效避免 SQL 注入）
                .filter(tableFieldInfo -> Objects.equals(property, tableFieldInfo.getProperty()))
                .map(TableFieldInfo::getColumn)
                .findFirst();
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static List<String> toColumns(final Class<?> clazz, final String[] properties) {
        return toColumns(clazz, CollectionUtils.toList(properties));
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static List<String> toColumns(final Class<?> clazz, final Collection<String> properties) {
        return getTableFieldInfos(clazz).stream()
                // 保证实体存在该属性（有效避免 SQL 注入）
                .filter(tableFieldInfo -> properties.contains(tableFieldInfo.getProperty()))
                .map(TableFieldInfo::getColumn)
                .collect(Collectors.toList());
    }

    /**
     * 获取 MP 缓存的所有表字段信息
     */
    protected static List<TableFieldInfo> getTableFieldInfos(final Class<?> clazz) {
        return Optional.ofNullable(TableInfoHelper.getTableInfo(clazz))
                .map(TableInfo::getFieldList)
                .orElseGet(ArrayList::new);
    }

    /**
     * 获取 MP 缓存的所有表字段
     */
    protected static List<String> getColumns(final Class<?> clazz) {
        return getTableFieldInfos(clazz).stream()
                .map(TableFieldInfo::getColumn)
                .collect(Collectors.toList());
    }

    /**
     * 获取实体属性值 Map
     *
     * @param entity 数据库实体对象
     * @return 数据库字段名与实体属性值之间的映射
     */
    protected static Map<String, Object> getFieldValueMap(final Object entity) {
        if (Objects.isNull(entity)) {
            return Collections.emptyMap();
        }
        return getFieldValueMap(entity.getClass(), entity);
    }

    /**
     * 获取实体属性值 Map
     *
     * @param clazz  数据库对应实体的类对象
     * @param entity 实体对象
     * @return 数据库字段名与实体属性值之间的映射
     */
    private static Map<String, Object> getFieldValueMap(final Class<?> clazz, final Object entity) {
        if (Objects.isNull(clazz) || Objects.isNull(entity)) {
            return Collections.emptyMap();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Collection<Method>> methodMap = METHOD_CACHE.computeIfAbsent(entityClass,
                c -> ReflectUtil.getFilteredPublicMethods(c, method -> ReflectUtil.isGetter(method, true))
                        .stream()
                        .collect(Collectors.groupingBy(ReflectUtil::getFieldNameFromGetterMethod,
                                ConcurrentHashMap::new,
                                Collectors.toCollection(ArrayList::new))));
        Map<String, Object> fieldValueMap = new HashMap<>();
        getFieldValuesMap(clazz, entity, methodMap).forEach((k, v) -> {
            Object value = v.stream().findFirst().orElse(null);
            fieldValueMap.put(k, value);
        });
        return fieldValueMap;
    }

    /**
     * 获取实体属性值 Map
     */
    protected static Map<String, Collection<Object>> getFieldValuesMap(final Class<?> clazz, final Object entity, final Map<String, Collection<Method>> methodMap) {
        return getTableFieldInfos(clazz).stream()
                .collect(Collectors.toMap(TableFieldInfo::getColumn,
                        tableFieldInfo -> Optional.ofNullable(tableFieldInfo.getProperty())
                                .map(methodMap::get)
                                .filter(CollectionUtils::isNotEmpty)
                                .map(Collection::stream)
                                .orElseGet(Stream::empty)
                                .map(method -> {
                                    try {
                                        return method.invoke(entity);
                                    } catch (Exception ignored) {
                                        return null;
                                    }
                                })
                                .collect(Collectors.toList()),
                        (oldVal, newVal) -> newVal,
                        HashMap::new));
    }

    protected QueryWrapperUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
