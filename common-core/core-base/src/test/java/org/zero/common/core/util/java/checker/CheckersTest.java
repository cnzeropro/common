package org.zero.common.core.util.java.checker;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.zero.common.core.support.checker.Checkers;
import org.zero.common.data.model.TestBean;

import java.util.Calendar;

/**
 * @author Zero
 * @since 2023/4/18
 */
@Slf4j
class CheckersTest {
    @Test
    void test() {
        TestBean testBean = TestBean.builder()
                .i(546)
                .date(DateTime.now())
                .calendar(Calendar.getInstance())
                .bigInteger(null)
                .build();

        Checkers.beanChecker(testBean)
                .notNull(TestBean::getBigInteger, "bigInteger不能为NULL")
                .present(TestBean::getDate, DatePattern.NORM_DATETIME_PATTERN, "date必须为现在的时间")
                .isPass();
    }
}