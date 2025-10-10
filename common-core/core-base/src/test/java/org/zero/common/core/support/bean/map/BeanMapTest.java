package org.zero.common.core.support.bean.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.zero.common.core.util.java.lang.JavaVersion;
import org.zero.common.core.util.java.util.MapUtil;
import org.zero.common.core.util.java.util.SetUtil;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/6
 */
class BeanMapTest {

    @Test
    void test() {
        Map<String, ?> map = BeanMap.of(new ValueToParamStringHandler(false,false))
                .objectConfig(ObjectConfig.of(new A(new A.B[]{new A.B(1, MapUtil.of("a", LocalDateTime.now())),
                                new A.B(Integer.MIN_VALUE, MapUtil.of("xxx", URI.create("https://www.baidu.com"),
                                        "yyy", SetUtil.of("abc", 3, new Error(), MapUtil.of(888, LocalDate.now()))))},
                                new A.C(new Date(), new A.C.D(new BigDecimal("1.1"))),
                                Arrays.asList(new A.E(JavaVersion.getCurrent(), Arrays.asList(1D, null,BigDecimal.TEN)),
                                        new A.E(JavaVersion.SIXTEEN, Collections.singleton(5L)), null),
                                new AntPathMatcher()))
                        .beanPackageLevelName("org.zero.common.core.util.BeanMapTest.A")
                        .ignoreNull(true)
                        .prefix("$"))
                .maxDeep(100)
                .toMap();
        System.out.println("size: " + map.size());
        map.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @ToString
    @AllArgsConstructor
    public static class A {
        B[] bs;
        C c;
        List<E> es;
        PathMatcher pathMatcher;

        @ToString
        @AllArgsConstructor
        public static class E {
            JavaVersion javaVersion;
            Collection<Number> numbers;
        }

        @ToString
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

            @ToString
            @AllArgsConstructor
            public static class D {
                BigDecimal bigDecimal;
            }
        }
    }
}