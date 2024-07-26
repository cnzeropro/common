package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
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
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Condition implements Serializable {
    /**
     * 条件字段
     */
    @NotEmpty
    private String column;
    /**
     * 操作符
     */
    private Operator operator = Operator.EQ;
    /**
     * 条件值
     */
    private String value;

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum Operator {
        /**
         * 等于：x = ?
         */
        EQ("%s = ?"),
        /**
         * 安全等于：x <=> ?
         */
        SEQ("%s <=> ?"),
        /**
         * 不等于：x [<>\!=] ?
         */
        NE("%s <> ?"),
        /**
         * 小于：x < ?
         */
        LT("%s < ?"),
        /**
         * 小于等于：x <= ?
         */
        LE("%s <= ?"),
        /**
         * 大于：x > ?
         */
        GT("%s > ?"),
        /**
         * 大于等于：x >= ?
         */
        GE("%s >= ?"),
        /**
         * 字符串模糊匹配：x LIKE ?
         */
        LIKE("%s LIKE ?", (Function<String, Object>) o -> String.format("%%%s%%", o)),
        /**
         * 字符串模糊不匹配：x NOT LIKE ?
         */
        NOT_LIKE("%s NOT LIKE ?", (Function<String, Object>) o -> String.format("%%%s%%", o)),
        /**
         * 字符串左模糊匹配：x LIKE ?
         */
        LEFT_LIKE("%s LIKE ?", (Function<String, Object>) o -> String.format("%%%s", o)),
        /**
         * 字符串左模糊不匹配：x NOT LIKE ?
         */
        NOT_LEFT_LIKE("%s NOT LIKE ?", (Function<String, Object>) o -> String.format("%%%s", o)),
        /**
         * 字符串右模糊匹配：x LIKE ?
         */
        RIGHT_LIKE("%s LIKE ?", (Function<String, Object>) o -> String.format("%s%%", o)),
        /**
         * 字符串右模糊不匹配：x NOT LIKE ?
         */
        NOT_RIGHT_LIKE("%s NOT LIKE ?", (Function<String, Object>) o -> String.format("%s%%", o)),
        /**
         * 集合匹配：x IN (?,?,...)
         */
        IN("%s IN %s", (UnaryOperator<String>) o -> {
            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            if (Objects.nonNull(o)) {
                String[] strings = StringUtils.commaDelimitedListToStringArray(o);
                for (String ignored : strings) {
                    stringJoiner.add("?");
                }
            }
            return stringJoiner.toString();
        }),
        /**
         * 集合不匹配：x NOT IN (?,?,...)
         */
        NOT_IN("%s NOT IN %s", (UnaryOperator<String>) o -> {
            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            if (Objects.nonNull(o)) {
                String[] strings = StringUtils.commaDelimitedListToStringArray(o);
                for (String ignored : strings) {
                    stringJoiner.add("?");
                }
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
        private UnaryOperator<String> sqlMapper;
        private Function<String, Object> paramMapper;

        Operator(String expression, UnaryOperator<String> sqlMapper) {
            this.expression = expression;
            this.sqlMapper = sqlMapper;
        }

        Operator(String expression, Function<String, Object> paramMapper) {
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
        String formattedParamStr;
        if (Objects.isNull(formattedParam)) {
            formattedParamStr = "";
        } else {
            formattedParamStr = formattedParam.toString();
        }
        // 占位符数量大于1，则需要将参数值按逗号分隔，并且将占位符设置为参数值
        int count = StringUtils.countOccurrencesOf(precompiledSql, "?");
        if (count > 1) {
            String sql = precompiledSql;
            String[] params = StringUtils.commaDelimitedListToStringArray(formattedParamStr);
            for (String param : params) {
                String paramStr = getParamStr(param);
                sql = PLACEHOLDER_PATTERN.matcher(sql).replaceFirst(paramStr);
            }
            return sql;
        }
        String paramStr = getParamStr(formattedParamStr);
        return precompiledSql.replace("?", paramStr);
    }

    static final Pattern NOT_NEED_QUOTE_PATTERN = Pattern.compile("^(true|false|\\d+(\\.\\d+)?)$", Pattern.CASE_INSENSITIVE);

    private String getParamStr(String param) {
        if (NOT_NEED_QUOTE_PATTERN.matcher(param).find()) {
            return param;
        } else {
            return String.format("'%s'", param);
        }
    }

    /**
     * 获取可以预编译的 SQL 片段，如：name LIKE ?
     */
    public String getPrecompiledSql() {
        String expression = operator.getExpression();
        UnaryOperator<String> sqlMapper = operator.getSqlMapper();
        Object[] args = new Object[]{column};
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
        Function<String, Object> paramMapper = operator.getParamMapper();
        if (Objects.isNull(value) || Objects.isNull(paramMapper)) {
            return value;
        }
        return paramMapper.apply(value);
    }
}
