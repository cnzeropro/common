package org.zero.common.core.util;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/6
 */
class BeanMapTest {

    @Test
    void tree() {
        BeanMap.of(BeanMap.ObjectConfig.of(new A(new A.B[]{new A.B(1, MapUtil.of("a", 1))},
                                new A.C(new Date(), new A.C.D(new BigDecimal("1.1")))))
                        .beanPackageLevelName("org.zero.common.core.util.BeanMapTest.A"))
                .toMap();
    }

    @Data
    @AllArgsConstructor
    public static class A {
        B[] bs;
        C c;

        @Data
        @AllArgsConstructor
        public static class B {
            int i;
            Map<String, Object> map;
        }

        @Data
        @AllArgsConstructor
        public static class C {
            Date date;
            D d;

            @Data
            @AllArgsConstructor
            public static class D {
                BigDecimal bigDecimal;
            }
        }
    }
}