package org.zero.common.data.constant;

/**
 * 通用常量
 *
 * @author zero
 * @since 2021/12/25
 */
public class CommonConstant {
    /**
     * 未删除
     */
    public static final int UNDELETED = 0;
    /**
     * 已删除
     */
    public static final int DELETED = 1;

    /**
     * 禁用
     */
    public static final int DISABLE = 0;
    /**
     * 启用
     */
    public static final int ENABLE = 1;

    /**
     * 默认每页大小
     */
    public static final String DEFAULT_PAGE_SIZE_STR = "10";
    /**
     * 默认每页大小（交由编译器优化）
     */
    // public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_SIZE = Integer.parseInt(DEFAULT_PAGE_SIZE_STR);

    private CommonConstant() throws IllegalAccessException {
        throw new IllegalAccessException("This class cannot be instantiated");
    }
}
