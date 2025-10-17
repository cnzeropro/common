package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.EnumUtil;
import org.zero.common.data.enumeration.UserStatus;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/4 17:04
 */
class EnumUtilTest {

    @Test
    void getEnumByName() {
		UserStatus status = EnumUtil.getEnumByName(UserStatus.class, "freeze");
        System.out.println(status);
    }

    @Test
    void getEnum() {
        UserStatus status = EnumUtil.getEnum(UserStatus.class, 1);
//        Status status = EnumUtil.getEnum(Status.class, "冻结");
        System.out.println(status);
    }

    @Test
    void getVal() {
        String key = EnumUtil.getVal(UserStatus.NORMAL, String.class);
        System.out.println(key);

        Object type = EnumUtil.getVal(UserStatus.NORMAL, "type");
        System.out.println(type);

        String name = EnumUtil.getVal(UserStatus.LOCKED, "name", String.class);
        System.out.println(name);
    }

    @Test
    void getOtherVal() {
        // Object name = EnumUtil.getOtherKey(Status.class, 3, "name");
        String name = EnumUtil.getOtherVal(UserStatus.class, 3, "name", String.class);
        System.out.println(name);
    }
}