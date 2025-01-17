package org.zero.common.core.util.mybatisflex;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.zero.common.core.util.java.reflect.ReflectUtil;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zero
 * @since 2024/7/15
 */
public class QueryWrapperUtil {
    public static final String SELECT_ALL = "*";

    /**
     * 方法缓存
     * <p>
     * 如有条件，建议换成三方内存缓存组件，像：Google Guava Cache、Caffeine Cache 等等。
     * 这些组件提供的 API 一般有更好的线程安全、缓存命中、数据一致性，并且缓存可控（大小，数量等等）
     */
    protected static final ConcurrentMap<Class<?>, Map<String, Collection<Method>>> METHOD_CACHE = new ConcurrentHashMap<>();

    public static <E> QueryWrapper setSelect(QueryWrapper queryWrapper, Class<E> clazz, String[] fields) {
        return setSelect(queryWrapper, clazz, CollectionUtil.newArrayList(fields));
    }

    public static <E> QueryWrapper setSelect(QueryWrapper queryWrapper, Class<E> clazz, Collection<String> fields) {
        List<String> columns;
        if (isSelectAll(fields)) {
            columns = getColumns(clazz);
        } else {
            columns = getFieldInfos(clazz, fields).stream()
                    .peek(fieldInfo -> {
                        if (!fieldInfo.isTableColumn()) {
                            checkSqlInjection(fieldInfo.getColumn());
                        }
                    })
                    .map(FieldInfo::getColumn)
                    .collect(Collectors.toList());
        }
        queryWrapper.select(columns.toArray(new String[0]));
        return queryWrapper;
    }

    public static <E> QueryWrapper setIsNull(QueryWrapper queryWrapper, Class<E> clazz, String[] fields) {
        return setIsNull(queryWrapper, clazz, CollectionUtil.newArrayList(fields));
    }

    public static <E> QueryWrapper setIsNull(QueryWrapper queryWrapper, Class<E> clazz, Collection<String> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return queryWrapper;
        }
        List<FieldInfo> fieldInfos = getFieldInfos(clazz, fields);
        fieldInfos.forEach(fieldInfo -> {
            String column = fieldInfo.getColumn();
            if (!fieldInfo.isTableColumn()) {
                checkSqlInjection(column);
            }
            queryWrapper.isNull(column, StringUtil.isNotBlank(column));
        });
        return queryWrapper;
    }

    public static <E> QueryWrapper setIsNotNull(QueryWrapper queryWrapper, Class<E> clazz, String[] fields) {
        return setIsNotNull(queryWrapper, clazz, CollectionUtil.newArrayList(fields));
    }

    public static <E> QueryWrapper setIsNotNull(QueryWrapper queryWrapper, Class<E> clazz, Collection<String> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return queryWrapper;
        }
        List<FieldInfo> fieldInfos = getFieldInfos(clazz, fields);
        fieldInfos.forEach(fieldInfo -> {
            String column = fieldInfo.getColumn();
            if (!fieldInfo.isTableColumn()) {
                checkSqlInjection(column);
            }
            queryWrapper.isNotNull(column, StringUtil.isNotBlank(column));
        });
        return queryWrapper;
    }

    public static <E> QueryWrapper setEq(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.eq(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setEq(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.eq(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setNe(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ne(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setNe(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ne(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setGt(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.gt(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setGt(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.gt(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setGe(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ge(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setGe(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.ge(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setLt(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.lt(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setLt(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.lt(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setLe(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.le(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setLe(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.le(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    /**
     * 模糊查询
     * <pre>
     * &lt;if test="property != null"&gt;
     *      &lt;bind name="propertyLike" value="'%' + property + '%'"/&gt;
     *      AND table.column LIKE #{propertyLike}
     * &lt;/if&gt;
     * </pre>
     */
    public static <E> QueryWrapper setLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.like(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.like(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setNotLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLike(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setNotLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLike(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setLeftLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeLeft(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setLeftLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeLeft(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setNotLeftLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeLeft(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setNotLeftLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeLeft(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setRightLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeRight(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setRightLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.likeRight(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setNotRightLike(QueryWrapper queryWrapper, E entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeRight(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E, T> QueryWrapper setNotRightLike(QueryWrapper queryWrapper, Class<E> clazz, T entity) {
        Map<String, Object> fieldValueMap = getFieldValueMap(clazz, entity);
        fieldValueMap.forEach((name, value) -> queryWrapper.notLikeRight(name, value, Objects.nonNull(value)));
        return queryWrapper;
    }

    public static <E> QueryWrapper setGroupBy(QueryWrapper queryWrapper, Class<E> clazz, String[] fields) {
        return setGroupBy(queryWrapper, clazz, CollectionUtil.newArrayList(fields));
    }

    public static <E> QueryWrapper setGroupBy(QueryWrapper queryWrapper, Class<E> clazz, Collection<String> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return queryWrapper;
        }
        List<FieldInfo> fieldInfos = getFieldInfos(clazz, fields);
        fieldInfos.forEach(fieldInfo -> {
            String column = fieldInfo.getColumn();
            if (!fieldInfo.isTableColumn()) {
                checkSqlInjection(column);
            }
            queryWrapper.groupBy(column);
        });
        return queryWrapper;
    }

    /**
     * SQL语法检查正则：符合两个关键字（有先后顺序）才算匹配
     */
    private static final Pattern SQL_SYNTAX_PATTERN = Pattern.compile("(insert|delete|update|select|create|drop|truncate|grant|alter|deny|revoke|call|execute|exec|declare|show|rename|set)" +
            "\\s+.*(into|from|set|where|table|database|view|index|on|cursor|procedure|trigger|for|password|union|and|or)|(select\\s*\\*\\s*from\\s+)|(and|or)\\s+.*(like|=|>|<|in|between|is|not|exists)", Pattern.CASE_INSENSITIVE);
    /**
     * 使用'、;或注释截断SQL检查正则
     */
    private static final Pattern SQL_COMMENT_PATTERN = Pattern.compile("'.*(or|union|--|#|/\\*|;)", Pattern.CASE_INSENSITIVE);

    /**
     * 检查是否存在 SQL 注入风险
     */
    protected static void checkSqlInjection(String str) {
        if (StringUtil.isNotBlank(str)) {
            if (SQL_COMMENT_PATTERN.matcher(str).find() || SQL_SYNTAX_PATTERN.matcher(str).find()) {
                throw new UtilException(String.format("There is a risk of SQL injection, please check: %s", str));
            }
        }
    }

    /**
     * 判断是否选择了所有字段
     */
    protected static boolean isSelectAll(final String[] strings) {
        return isSelectAll(CollectionUtil.newArrayList(strings));
    }

    /**
     * 判断是否选择了所有字段
     */
    protected static boolean isSelectAll(final Collection<String> strings) {
        if (CollectionUtil.isEmpty(strings)) {
            return true;
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
        return StringUtil.camelToUnderline(property);
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
        List<FieldInfo> tableFieldInfos = getTableFieldInfos(clazz);
        for (FieldInfo tableFieldInfo : tableFieldInfos) {
            if (Objects.equals(property, tableFieldInfo.getProperty())) {
                return Optional.ofNullable(tableFieldInfo.getColumn());
            }
        }
        return Optional.ofNullable(property);
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static List<String> toColumns(final Class<?> clazz, final String[] properties) {
        return toColumns(clazz, CollectionUtil.newArrayList(properties));
    }

    /**
     * 属性名（实体）转化为字段名（数据库）
     */
    protected static List<String> toColumns(final Class<?> clazz, final Collection<String> properties) {
        List<String> columns = new ArrayList<>(properties.size());
        List<FieldInfo> tableFieldInfos = getTableFieldInfos(clazz);
        for (String property : properties) {
            boolean isFound = false;
            for (FieldInfo tableFieldInfo : tableFieldInfos) {
                if (Objects.equals(property, tableFieldInfo.getProperty())) {
                    isFound = true;
                    columns.add(tableFieldInfo.getColumn());
                }
            }
            if (!isFound) {
                columns.add(property);
            }
        }
        return columns;
    }

    protected static List<FieldInfo> getFieldInfos(final Class<?> clazz, final Collection<String> fields) {
        List<FieldInfo> fieldInfos = new ArrayList<>(fields.size());
        List<FieldInfo> tableFieldInfos = getTableFieldInfos(clazz);
        for (String field : fields) {
            FieldInfo fieldInfo = new FieldInfo(field);
            for (FieldInfo tableFieldInfo : tableFieldInfos) {
                if (Objects.equals(field, tableFieldInfo.getProperty())) {
                    fieldInfo.setColumn(tableFieldInfo.getColumn());
                }
            }
            fieldInfos.add(fieldInfo);
        }
        return fieldInfos;
    }

    /**
     * 获取 MF 缓存的所有表字段信息
     */
    protected static List<FieldInfo> getTableFieldInfos(final Class<?> clazz) {
        List<FieldInfo> fieldInfos = new ArrayList<>();
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        // 塞入列
        tableInfo.getColumnInfoList()
                .stream()
                .map(columnInfo -> new FieldInfo(columnInfo.getProperty(), columnInfo.getColumn()))
                .forEach(fieldInfos::add);
        // 塞入主键
        tableInfo.getPrimaryKeyList()
                .stream()
                .map(idInfo -> new FieldInfo(idInfo.getProperty(), idInfo.getColumn()))
                .forEach(fieldInfos::add);
        return fieldInfos;
    }

    @Data
    @AllArgsConstructor
    protected static class FieldInfo {
        private String property;
        private String column;

        public FieldInfo(String field) {
            this.property = field;
            this.column = field;
        }

        /**
         * 当属性名和字段名不一致时，认为是数据库字段
         */
        public boolean isTableColumn() {
            return !Objects.equals(property, column);
        }
    }

    /**
     * 获取 MP 缓存的所有表字段
     */
    protected static List<String> getColumns(final Class<?> clazz) {
        return getTableFieldInfos(clazz).stream()
                .map(FieldInfo::getColumn)
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
                c -> ReflectUtil.getFilteredPublicMethods(c, method -> ReflectUtil.isGetter(method, false))
                        .stream()
                        .collect(Collectors.groupingBy(ReflectUtil::getFieldNameFromGetterMethod,
                                ConcurrentHashMap::new,
                                Collectors.toCollection(ArrayList::new))));
        Map<String, Object> fieldValueMap = new HashMap<>();
        getFieldValuesMap(clazz, entity, methodMap).forEach((k, v) -> {
            // 当第一个元素为 null 时，Stream.findFirst 可能抛出 NPE，因此不建议使用
            Object value = null;
            Iterator<Object> iterator = v.iterator();
            if (iterator.hasNext()) {
                value = iterator.next();
            }
            fieldValueMap.put(k, value);
        });
        return fieldValueMap;
    }

    /**
     * 获取实体属性值 Map
     */
    protected static Map<String, Collection<Object>> getFieldValuesMap(final Class<?> clazz, final Object entity, final Map<String, Collection<Method>> methodMap) {
        return getTableFieldInfos(clazz).stream()
                .collect(Collectors.toMap(FieldInfo::getColumn,
                        tableFieldInfo -> Optional.ofNullable(tableFieldInfo.getProperty())
                                .map(methodMap::get)
                                .filter(CollectionUtil::isNotEmpty)
                                .map(Collection::stream)
                                .orElseGet(Stream::empty)
                                .map(method -> ReflectUtil.invoke(method, entity))
                                .collect(Collectors.toList()),
                        (oldVal, newVal) -> newVal,
                        HashMap::new));
    }

    protected QueryWrapperUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
