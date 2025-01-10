package org.zero.common.core.support.common.query.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.common.query.sql.ComparisonOperator;
import org.zero.common.data.model.query.Operator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/3
 */
public class StringToOperatorConverter implements Converter<String, Operator> {
    @Override
    public Operator convert(String source) {
        if (!StringUtils.hasText(source)) {
            return ComparisonOperator.EQ;
        }

        return convertInternal(source);
    }

    protected Map<String, Collection<Operator>> operatorMap = new HashMap<>();

    /**
     * 如有需要，可重写此方法
     * <p>
     * 如：
     * <pre>{@code
     * @Override
     * protected Operator convertInternal(String name) {
     *   // 新的转换逻辑
     *   // some code...
     *   // 未正确转换时使用默认逻辑尝试
     *   return super.convertInternal(name);
     * }
     * }</pre>
     */
    protected Operator convertInternal(String name) {
        return operatorMap.compute(name, (k, v) -> {
                    Collection<Operator> operators = v;
                    if (Objects.isNull(v)) {
                        operators = new TreeSet<>(Comparator.comparingInt(Operator::getOrder));
                    }
                    this.getComparisonOperator(k)
                            .ifPresent(operators::add);
                    return operators;
                })
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Operator [%s] is not supported", name)));
    }

    private Optional<? extends Operator> getComparisonOperator(String name) {
        return Arrays.stream(ComparisonOperator.values())
                .filter(comparisonOperator -> comparisonOperator.name().equals(name))
                .findFirst();
    }
}
