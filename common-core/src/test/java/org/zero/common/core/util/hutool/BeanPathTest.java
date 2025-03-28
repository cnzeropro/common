package org.zero.common.core.util.hutool;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/19
 */
class BeanPathTest {
    BeanPath beanPath;
    Object bean;
    Object value;

    @BeforeEach
    void setUp() {
        String expression = "c.d.bigDecimal";
        beanPath = BeanPath.parse(expression);
        bean = new A();
        value = BigDecimal.TEN;
    }

    // @BeforeEach
    void setUp1() {
        String expression = "[3].   .a[0][1][2].b['2025'].c[fff].d[5]['2'][0].e";
        beanPath = BeanPath.parse(expression);
        bean = ListUtil.list(true);
        value = new Date();
    }

    // @BeforeEach
    void setUp2() {
        String expression = "a.b[2][0].c['0'].d";
        beanPath = BeanPath.parse(expression);
        bean = MapUtil.newHashMap(false);
        value = Thread.currentThread();
    }

    @Test
    void test() {
        // set
        beanPath.set(bean, value);
        System.out.println(bean);

        // get
        Object obj = beanPath.get(bean);
        System.out.println(obj);
    }

    class A {
        B[] bs;
        C c;

        class B {
            int i;
            Map<String, Object> map = MapUtil.of("a", 1);
        }

        class C {
            Date date;
            D d;

            class D {
                BigDecimal bigDecimal;
            }
        }
    }
}