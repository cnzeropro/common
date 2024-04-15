package org.zero.common.data.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/8/26 11:30
 */
@Getter
@AllArgsConstructor
public enum Status {
    NORMAL(1, "正常"),
    LOCKED(2, "锁定"),
    FREEZING(3, "冻结"),
    LOST(4, "挂失"),
    DELETED(5, "销户");

    private final Integer type;
    private final String name;
}
