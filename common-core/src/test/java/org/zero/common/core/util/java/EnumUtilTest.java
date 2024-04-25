package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Status;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/4 17:04
 */
class EnumUtilTest {

    @Test
    void getEnumByName() {
        Status status = EnumUtil.getEnumByName(Status.class, "freeze");
        System.out.println(status);
    }

    @Test
    void getEnum() {
        Status status = EnumUtil.getEnum(Status.class, 1);
//        Status status = EnumUtil.getEnum(Status.class, "冻结");
        System.out.println(status);
    }

    @Test
    void getVal() {
        String key = EnumUtil.getVal(Status.NORMAL, String.class);
        System.out.println(key);

        Object type = EnumUtil.getVal(Status.NORMAL, "type");
        System.out.println(type);

        String name = EnumUtil.getVal(Status.LOCKED, "name", String.class);
        System.out.println(name);
    }

    @Test
    void getOtherVal() {
        // Object name = EnumUtil.getOtherKey(Status.class, 3, "name");
        String name = EnumUtil.getOtherVal(Status.class, 3, "name", String.class);
        System.out.println(name);
    }
}