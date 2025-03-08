package org.zero.common.data.constant;

import lombok.experimental.UtilityClass;

/**
 * 通用常量
 *
 * @author zero
 * @since 2021/12/25
 */
@UtilityClass
public class CommonConstant {
    /**
     * 默认每页大小
     */
    public static final String DEFAULT_PAGE_SIZE_STR = "10";
    /**
     * 默认每页大小（交由编译器优化）
     */
    // public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_SIZE = Integer.parseInt(DEFAULT_PAGE_SIZE_STR);
}
