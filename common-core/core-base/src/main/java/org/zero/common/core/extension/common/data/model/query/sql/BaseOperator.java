package org.zero.common.core.extension.common.data.model.query.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.model.query.Operator;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/3
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class BaseOperator implements Operator {
    private final String template;
    private Collection<Function<Object, String>> statementMappers;
    private Collection<UnaryOperator<Object>> paramMappers;
}
