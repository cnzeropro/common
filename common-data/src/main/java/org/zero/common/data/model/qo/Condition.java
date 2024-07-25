package org.zero.common.data.model.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
        LIKE("%s LIKE ?", o -> String.format("%%%s%%", o)),
        /**
         * 字符串模糊不匹配：x NOT LIKE ?
         */
        NOT_LIKE("%s NOT LIKE ?", o -> String.format("%%%s%%", o)),
        /**
         * 字符串左模糊匹配：x LIKE ?
         */
        LEFT_LIKE("%s LIKE ?", o -> String.format("%%%s", o)),
        /**
         * 字符串左模糊不匹配：x NOT LIKE ?
         */
        NOT_LEFT_LIKE("%s NOT LIKE ?", o -> String.format("%%%s", o)),
        /**
         * 字符串右模糊匹配：x LIKE ?
         */
        RIGHT_LIKE("%s LIKE ?", o -> String.format("%s%%", o)),
        /**
         * 字符串右模糊不匹配：x NOT LIKE ?
         */
        NOT_RIGHT_LIKE("%s NOT LIKE ?", o -> String.format("%s%%", o)),
        /**
         * 集合匹配：x IN (?,?,...)
         */
        IN("%s IN %s", (Function<Object, Object>) o -> {
            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            if (ObjectUtils.isArray(o)) {
                Object[] array = (Object[]) o;
                for (Object ignored : array) {
                    stringJoiner.add("?");
                }
            } else if (o instanceof Collection) {
                Collection<?> collection = (Collection<?>) o;
                for (Object ignored : collection) {
                    stringJoiner.add("?");
                }
            }
            return stringJoiner.toString();
        }),
        /**
         * 集合不匹配：x NOT IN (?,?,...)
         */
        NOT_IN("%s NOT IN %s", (Function<Object, Object>) o -> {
            StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
            if (ObjectUtils.isArray(o)) {
                Object[] array = (Object[]) o;
                for (Object ignored : array) {
                    stringJoiner.add("?");
                }
            } else if (o instanceof Collection) {
                Collection<?> collection = (Collection<?>) o;
                for (Object ignored : collection) {
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
        private Function<Object, Object> sqlMapper;
        private UnaryOperator<Object> paramMapper;

        public String getPrecompiledSql(String column) {
            return String.format(expression, column);
        }

        public String getPrecompiledSql(String column, Object value) {
            Object[] args = new Object[]{column};
            if (Objects.nonNull(sqlMapper)) {
                Object applied = sqlMapper.apply(value);
                args = ObjectUtils.addObjectToArray(args, applied);
            }
            return String.format(expression, args);
        }

        public Object getFormattedParam(Object value) {
            if (Objects.isNull(paramMapper)) {
                return null;
            }
            return paramMapper.apply(value);
        }

        Operator(String expression, Function<Object, Object> sqlMapper) {
            this.expression = expression;
            this.sqlMapper = sqlMapper;
        }

        Operator(String expression, UnaryOperator<Object> paramMapper) {
            this.expression = expression;
            this.paramMapper = paramMapper;
        }
    }
}
