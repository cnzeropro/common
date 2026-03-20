package org.zero.common.api.extra.loki.model.common;

import java.util.ArrayList;

/**
 * Loki 值数组抽象基类。
 * <p>
 * 对应 Loki 返回或提交时的数组结构，例如 {@code [timestamp, line]}、
 * {@code [timestamp, value]} 或 {@code [timestamp, line, structuredMetadata]}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
public abstract class LokiValue extends ArrayList<Object> {
    protected LokiValue() {
        this(2);
    }

    protected LokiValue(int initialCapacity) {
        super(initialCapacity);
    }
}
