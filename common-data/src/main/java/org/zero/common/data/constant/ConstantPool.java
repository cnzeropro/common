package org.zero.common.data.constant;

/**
 * 综合常量池。
 * <p>
 * 聚合 {@link StringPool} 与 {@link CharPool}，并补充各基础类型的零值常量，
 * 用于避免在业务代码中出现魔法数字（magic number）。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public interface ConstantPool extends StringPool, CharPool {
    /**
     * byte 零值。
     */
    byte BYTE_ZERO = (byte) 0;
    /**
     * short 零值。
     */
    short SHORT_ZERO = (short) 0;
    /**
     * int 零值。
     */
    int INT_ZERO = 0;
    /**
     * long 零值。
     */
    long LONG_ZERO = 0L;
    /**
     * float 零值。
     */
    float FLOAT_ZERO = 0.0F;
    /**
     * double 零值。
     */
    double DOUBLE_ZERO = 0.0D;
    /**
     * char 空字符（{@code '\u0000'}）。
     */
    char CHAR_ZERO = '\u0000';
    /**
     * 布尔值 {@code false}。
     */
    boolean BOOLEAN_FALSE = false;
    /**
     * 布尔值 {@code true}。
     */
    boolean BOOLEAN_TRUE = true;
}
