package org.zero.common.core.extension.common.query.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.model.query.Operator;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/3
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum LikeOperator implements Operator {
    /**
     * 字符串模糊匹配：x LIKE ?
     */
    ALL_LIKE("%s LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%%%s%%", o))),
    /**
     * 字符串模糊不匹配：x NOT LIKE ?
     */
    NOT_ALL_LIKE("%s NOT LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%%%s%%", o))),
    /**
     * 字符串左模糊匹配：x LIKE ?
     */
    LEFT_LIKE("%s LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%%%s", o))),
    /**
     * 字符串左模糊不匹配：x NOT LIKE ?
     */
    NOT_LEFT_LIKE("%s NOT LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%%%s", o))),
    /**
     * 字符串右模糊匹配：x LIKE ?
     */
    RIGHT_LIKE("%s LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%s%%", o))),
    /**
     * 字符串右模糊不匹配：x NOT LIKE ?
     */
    NOT_RIGHT_LIKE("%s NOT LIKE ?",
            Collections.emptyList(),
            Collections.singletonList(o -> String.format("%s%%", o))),
    ;

    private final String template;
    private Collection<Function<Object, String>> statementMappers;
    private Collection<UnaryOperator<Object>> paramMappers;
}
