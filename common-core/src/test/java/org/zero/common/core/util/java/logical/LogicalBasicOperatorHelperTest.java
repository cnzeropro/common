package org.zero.common.core.util.java.logical;

import cn.hutool.core.date.DateTime;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.LogicalOperatorHelper;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/4/25
 */
class LogicalBasicOperatorHelperTest {
    @Test
    void result() {
        boolean result = LogicalOperatorHelper.init(DateTime.now(), DateTime::isAM)
                .or(t -> t > 100, 34, 776)
                .negate()
                .result();
        System.out.println(result);
    }
}