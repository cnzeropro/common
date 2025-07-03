package org.zero.common.api.extra.loki.model.common;

import java.util.ArrayList;

/**
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
