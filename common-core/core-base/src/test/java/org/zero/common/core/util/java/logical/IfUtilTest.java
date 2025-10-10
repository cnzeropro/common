package org.zero.common.core.util.java.logical;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.IfUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/30
 */
@Slf4j
class IfUtilTest {

    @Test
    void map() {
        BigDecimal number = IfUtil.map(1.1, 2.5, d -> BigDecimal.valueOf(d), true, false, true);
        System.out.println(number);

        BigDecimal number1 = IfUtil.mapOrGet(1, BigDecimal::valueOf, BigDecimal.ZERO, () -> true, () -> false, () -> true);
        System.out.println(number1);
    }

    @Test
    void get() {
        Date date = IfUtil.provide(Date::new, new Date(1), () -> true, () -> true, () -> true);
        System.out.println(date);
    }

    @Test
    void deal() {
        IfUtil.consume(log, l -> l.warn("测试使用"), true);
        IfUtil.consume(1, 2, System.out::println, () -> true, () -> false, () -> true);
    }

    @Test
    void test() {
        boolean bool = IfUtil.test(0, 2, i -> i == 0, () -> true, () -> true, () -> true);
        System.out.println(bool);
    }
}