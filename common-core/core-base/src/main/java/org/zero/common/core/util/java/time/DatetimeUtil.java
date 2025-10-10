package org.zero.common.core.util.java.time;

import lombok.experimental.UtilityClass;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@UtilityClass
public class DatetimeUtil {
    /**
     * 根据蔡勒公式计算任意一个日期是星期几
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 中国星期（从星期一开始，星期日是7）
     */
    public static int calculateWeek(int year, int month, int day) {
        if (month == 1) {
            month = 13;
            year--;
        }
        if (month == 2) {
            month = 14;
            year--;
        }
        int y = year % 100;
        int c = year / 100;
        int h = (y + (y / 4) + (c / 4) - (2 * c) + ((26 * (month + 1)) / 10) + day - 1) % 7;
        // 可能是负值，因此计算除以7的余数之后需要判断是大于等于0还是小于0，如果小于0则将余数加7
        if (h < 0) {
            h += 7;
        }

        // 国内理解中星期日为 7
        if (h == 0) {
            return 7;
        }
        return h;
    }
}
