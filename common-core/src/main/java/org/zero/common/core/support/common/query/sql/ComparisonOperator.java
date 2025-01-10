package org.zero.common.core.support.common.query.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.zero.common.data.model.query.Operator;

import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/3
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum ComparisonOperator implements Operator {
    /**
     * 等于：x = ?
     */
    EQ("%s = ?"),
    /**
     * NULL 值安全相等：x IS NOT DISTINCT FROM y
     */
    NULL_EQ("%s IS NOT DISTINCT FROM %s"),
    /**
     * 不等于：x &lt;&gt; ?
     */
    NE("%s <> ?"),
    /**
     * NULL 值安全不相等：x IS DISTINCT FROM y
     */
    NULL_NE("%s IS DISTINCT FROM %s"),
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
     * NULL 值匹配：x IS NULL
     */
    IS_NULL("%s IS NULL"),
    /**
     * NULL 值不匹配：x IS NOT NULL
     */
    IS_NOT_NULL("%s IS NOT NULL"),
    /**
     * 字符串模糊匹配：x LIKE ?
     */
    LIKE("%s LIKE ? ESCAPE '\\'",
            Collections.emptyList(),
            Collections.singletonList(o -> {
                String str = String.valueOf(o);
                String replaced = str.replace("\\", "\\\\");
                return String.format("%%%s%%", replaced);
            })),
    /**
     * 字符串模糊不匹配：x NOT LIKE ?
     */
    NOT_LIKE("%s NOT LIKE ? ESCAPE '\\'",
            Collections.emptyList(),
            Collections.singletonList(o -> {
                String str = String.valueOf(o);
                String replaced = str.replace("\\", "\\\\");
                return String.format("%%%s%%", replaced);
            })),
    /**
     * 集合匹配：x IN (?,?,...)
     */
    IN("%s IN %s",
            Collections.singletonList(o -> {
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
            Collections.emptyList()),
    /**
     * 集合不匹配：x NOT IN (?,?,...)
     */
    NOT_IN("%s NOT IN %s",
            Collections.singletonList(o -> {
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
            Collections.emptyList()),
    ;

    private final String template;
    private Collection<Function<Object, String>> statementMappers;
    private Collection<UnaryOperator<Object>> paramMappers;
}
