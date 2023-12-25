package org.zero.common.data.constant;

/**
 * @author zero
 * @since 2021/12/25
 */
public class CommonConstant {
    public static final int UNDELETED = 0;
    public static final int DELETED = 1;

    private CommonConstant() throws IllegalAccessException {
        throw new IllegalAccessException("This class cannot be instantiated");
    }
}
