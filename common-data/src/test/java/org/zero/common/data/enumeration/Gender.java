package org.zero.common.data.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/26
 */
@Getter
@AllArgsConstructor
public enum Gender {
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    private final Integer type;
    private final String name;
}
