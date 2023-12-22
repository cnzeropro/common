package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.zero.common.data.constant.StatusEnum;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/4 17:04
 */
class EnumUtilTest {

    @Test
    void getEnumByName() {
        StatusEnum statusEnum = EnumUtil.getEnumByName(StatusEnum.class, "lost");
        System.out.println(statusEnum);
    }

    @Test
    void getEnum() {
        StatusEnum statusEnum = EnumUtil.getEnum(StatusEnum.class, 1);
//        StatusEnum statusEnum = EnumUtil.getEnum(StatusEnum.class, "冻结");
        System.out.println(statusEnum);
    }

    @Test
    void getKey() {
        String key = EnumUtil.getVal(StatusEnum.DELETED, String.class);
        System.out.println(key);

        Object statusId = EnumUtil.getVal(StatusEnum.LOST, "statusId");
        System.out.println(statusId);

        String genderName = EnumUtil.getVal(StatusEnum.LOCKED, "statusName", String.class);
        System.out.println(genderName);
    }

    @Test
    void getOtherKey() {
        // Object statusName = EnumUtil.getOtherKey(StatusEnum.class, 1, "statusName");
        String statusName = EnumUtil.getOtherVal(StatusEnum.class, 4, "statusName", String.class);
        System.out.println(statusName);
    }
}