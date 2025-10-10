package org.zero.common.core.util.mybatisplus;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.mybatisplus.annotation.MpEnumUtil;
import org.zero.common.data.enumeration.Gender;
import org.zero.common.data.enumeration.Status;

import java.lang.reflect.Method;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
class MpEnumUtilTest {
    @Test
    void test() {
        Method method = MpEnumUtil.getMethod(Gender.class);
        System.out.println(method);

        Integer value = MpEnumUtil.getValue(Status.FREEZE, Integer.class);
        System.out.println(value);
    }
}