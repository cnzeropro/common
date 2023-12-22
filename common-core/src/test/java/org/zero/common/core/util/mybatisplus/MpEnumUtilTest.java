package org.zero.common.core.util.mybatisplus;

import org.junit.jupiter.api.Test;
import org.zero.common.data.constant.StatusEnum;

import java.lang.reflect.Method;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
class MpEnumUtilTest {
    @Test
    void test() {
        Method method = MpEnumUtil.getMethod(StatusEnum.class);
        System.out.println(method);
    }
}