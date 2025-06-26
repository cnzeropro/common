package org.zero.common.core.util.java.reflect;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.reflect.LambdaMeta;
import org.zero.common.core.util.java.lang.reflect.LambdaUtil;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
class LambdaUtilTest {

    @Test
    void extract() {
        LambdaMeta lambdaMeta = LambdaUtil.extract((SFunction<Timestamp, Long>) Date::getTime);
        System.out.println(lambdaMeta.getImplMethodName());
        System.out.println(lambdaMeta.getImplClass());
        System.out.println(lambdaMeta.getInstantiatedClass());

        int i = 1;
        Comparator<Integer> comparator = (i1, i2) -> i;
        lambdaMeta = LambdaUtil.extract(comparator);
        System.out.println(lambdaMeta.getImplMethodName());
        System.out.println(lambdaMeta.getImplClass());
        System.out.println(lambdaMeta.getInstantiatedClass());

        lambdaMeta = LambdaUtil.extract((Function<List<?>, Stream<?>>) Collection::stream);
        System.out.println(lambdaMeta.getImplMethodName());
        System.out.println(lambdaMeta.getImplClass());
        System.out.println(lambdaMeta.getInstantiatedClass());

        lambdaMeta = LambdaUtil.extract("a");
        System.out.println(lambdaMeta.getImplMethodName());
        System.out.println(lambdaMeta.getImplClass());
        System.out.println(lambdaMeta.getInstantiatedClass());
    }

    @Test
    void isLambda() {
        System.out.println(LambdaUtil.isLambda((Function<Timestamp, Long>) Date::getTime));
    }
}