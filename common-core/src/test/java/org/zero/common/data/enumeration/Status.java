package org.zero.common.data.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
    FREEZE(3, "冻结"),
    ;

    @EnumValue
    private final Integer type;
    private final String name;
}
