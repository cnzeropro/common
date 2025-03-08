package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/8/26 11:30
 */
@Getter
@RequiredArgsConstructor
public enum Status {
    NORMAL(1, "正常"),
    LOCKED(2, "锁定"),
    FREEZE(3, "冻结"),
    ;

    private final Integer type;
    private final String name;
}
