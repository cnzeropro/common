package org.zero.common.core.extension.common.query.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.enumeration.Operator;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum AssignmentOperator implements Operator {
    ASSIGN("%s := %s"),
    SET("%s = ?"),
    ;

    private final String template;
    private Collection<Function<Object, String>> statementMappers;
    private Collection<UnaryOperator<Object>> paramMappers;
}
