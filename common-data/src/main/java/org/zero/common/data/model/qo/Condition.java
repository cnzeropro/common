package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @author zero
 * @since 2024/6/20
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Condition implements Serializable {
    /**
     * 条件字段
     */
    @NotEmpty
    private String field;
    /**
     * 操作符
     */
    private Operator operator = Operator.EQ;
    /**
     * 条件值
     */
    private Object value;

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum Operator {
        /**
         * 等于：x = ?
         */
        EQ("%s = ?"),
        /**
         * 安全等于：x &lt;=&gt; ?
         */
        SEQ("%s <=> ?"),
        /**
         * 不等于：x [&lt;&gt;\!=] ?
         */
        NE("%s <> ?"),
        /**
         * 小于：x &lt; ?
         */
        LT("%s < ?"),
        /**
         * 小于等于：x &lt;= ?
         */
        LE("%s <= ?"),
        /**
         * 大于：x &gt; ?
         */
        GT("%s > ?"),
        /**
         * 大于等于：x &gt;= ?
         */
        GE("%s >= ?"),
        /**
         * 字符串模糊匹配：x LIKE ?
         */
        LIKE("%s LIKE ?", (UnaryOperator<Object>) o -> String.format("%%%s%%", o)),
        /**
         * 字符串模糊不匹配：x NOT LIKE ?
         */
        NOT_LIKE("%s NOT LIKE ?", (UnaryOperator<Object>) o -> String.format("%%%s%%", o)),
        /**
         * 字符串左模糊匹配：x LIKE ?
         */
        LEFT_LIKE("%s LIKE ?", (UnaryOperator<Object>) o -> String.format("%%%s", o)),
        /**
         * 字符串左模糊不匹配：x NOT LIKE ?
         */
        NOT_LEFT_LIKE("%s NOT LIKE ?", (UnaryOperator<Object>) o -> String.format("%%%s", o)),
        /**
         * 字符串右模糊匹配：x LIKE ?
         */
        RIGHT_LIKE("%s LIKE ?", (UnaryOperator<Object>) o -> String.format("%s%%", o)),
        /**
         * 字符串右模糊不匹配：x NOT LIKE ?
         */
        NOT_RIGHT_LIKE("%s NOT LIKE ?", (UnaryOperator<Object>) o -> String.format("%s%%", o)),
        /**
         * 集合匹配：x IN (?,?,...)
         */
        IN("%s IN %s", (Function<Object, String>) o -> {
            Object[] objects = {o};
            if (ObjectUtils.isArray(o)) {
                objects = (Object[]) o;
            } else if (o instanceof Collection) {
                Collection<?> collection = (Collection<?>) o;
                objects = collection.toArray();
            } else if (o instanceof String) {
                String str = (String) o;
                if (str.contains(",")) {
                    objects = StringUtils.delimitedListToStringArray(str, ",");
                }
            }

            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            for (Object ignored : objects) {
                stringJoiner.add("?");
            }
            return stringJoiner.toString();
        }),
        /**
         * 集合不匹配：x NOT IN (?,?,...)
         */
        NOT_IN("%s NOT IN %s", (Function<Object, String>) o -> {
            Object[] objects = {o};
            if (ObjectUtils.isArray(o)) {
                objects = (Object[]) o;
            } else if (o instanceof Collection) {
                Collection<?> collection = (Collection<?>) o;
                objects = collection.toArray();
            } else if (o instanceof String) {
                String str = (String) o;
                if (str.contains(",")) {
                    objects = StringUtils.delimitedListToStringArray(str, ",");
                }
            }

            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            for (Object ignored : objects) {
                stringJoiner.add("?");
            }
            return stringJoiner.toString();
        }),
        /**
         * 范围匹配：x BETWEEN ? AND ?
         */
        BETWEEN_AND("%s BETWEEN ? AND ?"),
        /**
         * 范围不匹配：x NOT BETWEEN ? AND ?
         */
        NOT_BETWEEN_AND("%s NOT BETWEEN ? AND ?"),
        /**
         * 条件匹配：x IS ?
         */
        IS("%s IS ?"),
        /**
         * 条件不匹配：x IS NOT ?
         */
        IS_NOT("%s IS NOT ?"),
        /**
         * NULL值匹配：x IS NULL
         */
        IS_NULL("%s IS NULL"),
        /**
         * NULL值不匹配：x IS NOT NULL
         */
        IS_NOT_NULL("%s IS NOT NULL"),
        /**
         * 正则匹配：x [REGEXP\RLIKE] ?
         */
        REGEXP("%s REGEXP ?"),
        /**
         * 正则区分大小写匹配：x REGEXP BINARY ?
         */
        REGEXP_BINARY("%s REGEXP BINARY ?"),
        ;

        private final String expression;
        private Function<Object, String> sqlMapper;
        private UnaryOperator<Object> paramMapper;

        Operator(String expression, Function<Object, String> sqlMapper) {
            this.expression = expression;
            this.sqlMapper = sqlMapper;
        }

        Operator(String expression, UnaryOperator<Object> paramMapper) {
            this.expression = expression;
            this.paramMapper = paramMapper;
        }
    }

    static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\?");

    /**
     * 获取 SQL 片段，如：name LIKE '%a%'
     */
    public String getSql() {
        String precompiledSql = getPrecompiledSql();
        Object formattedParam = getFormattedParam();

        if (!precompiledSql.contains("?")) {
            return precompiledSql;
        }

        Object[] params = {formattedParam};
        if (formattedParam instanceof String) {
            String str = (String) formattedParam;
            if (str.contains(",")) {
                params = StringUtils.delimitedListToStringArray(str, ",");
            }
        } else if (ObjectUtils.isArray(formattedParam)) {
            params = (Object[]) formattedParam;
        } else if (formattedParam instanceof Collection) {
            Collection<?> collection = (Collection<?>) formattedParam;
            params = collection.toArray();
        }

        String sql = precompiledSql;
        for (Object param : params) {
            String paramStr = getParamStr(param);
            sql = PLACEHOLDER_PATTERN.matcher(sql)
                    .replaceFirst(paramStr);
        }
        return sql;
    }

    static final Pattern NOT_NEED_QUOTE_PATTERN = Pattern.compile("^(true|false|\\d+(\\.\\d+)?)$", Pattern.CASE_INSENSITIVE);

    private String getParamStr(Object param) {
        if (Objects.isNull(param)) {
            return "null";
        }

        Class<?> clazz = param.getClass();
        if (isNumClass(clazz)) {
            return param.toString();
        } else {
            String str = param.toString();
            if (NOT_NEED_QUOTE_PATTERN.matcher(str).find()) {
                return str;
            } else {
                return String.format("'%s'", param);
            }
        }
    }

    /**
     * 是否是数字类型
     */
    static boolean isNumClass(Class<?> clazz) {
        return Objects.nonNull(clazz) &&
                (Number.class.isAssignableFrom(clazz) ||
                        (clazz.isPrimitive() &&
                                (clazz == int.class || clazz == long.class ||
                                        clazz == short.class || clazz == byte.class ||
                                        clazz == float.class || clazz == double.class)));
    }

    /**
     * 获取可以预编译的 SQL 片段，如：name LIKE ?
     */
    public String getPrecompiledSql() {
        String expression = operator.getExpression();
        Function<Object, String> sqlMapper = operator.getSqlMapper();
        Object[] args = new Object[]{field};
        if (Objects.nonNull(value) && Objects.nonNull(sqlMapper)) {
            Object applied = sqlMapper.apply(value);
            args = ObjectUtils.addObjectToArray(args, applied);
        }
        return String.format(expression, args);
    }

    /**
     * 获取处理的 SQL 参数，如：%a%
     */
    public Object getFormattedParam() {
        UnaryOperator<Object> paramMapper = operator.getParamMapper();
        if (Objects.isNull(value) || Objects.isNull(paramMapper)) {
            return value;
        }
        return paramMapper.apply(value);
    }
}
