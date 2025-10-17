package org.zero.common.data.enumeration;

import org.zero.common.data.model.util.Ordered;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * SQL 操作符
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/3
 */
public interface Operator extends Ordered {
    /**
     * 表达式模板。如：{@code %s = ?}
     */
    String getTemplate();

    /**
     * 表达式语句处理。如：模板（{@code %s IN %s}）中的第二个占位符需要特殊处理为 {@code (?, ?, ?)}
     */
    Collection<Function<Object, String>> getStatementMappers();

    /**
     * 表达式参数处理。如：模板（{@code %s LIKE ?}）中的参数需要特殊处理为 {@code %param%}
     */
    Collection<UnaryOperator<Object>> getParamMappers();
}
