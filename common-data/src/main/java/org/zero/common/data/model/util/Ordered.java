package org.zero.common.data.model.util;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public interface Ordered {
    int DEFAULT_ORDER = 0;
    int HIGHEST_ORDER = Integer.MAX_VALUE;
    int LOWEST_ORDER = Integer.MIN_VALUE;

    default int order() {
        return DEFAULT_ORDER;
    }
}
