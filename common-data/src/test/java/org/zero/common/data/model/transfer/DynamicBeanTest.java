package org.zero.common.data.model.transfer;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
class DynamicBeanTest {
    @Test
    void get() {
        DynamicBean bean = DynamicBean.create()
                .set("name", "zero")
                .set("age", 18);

        String name = bean.get("name", String.class);
        System.out.println(name);
        int age = bean.getInt("age");
        System.out.println(age);
    }
}