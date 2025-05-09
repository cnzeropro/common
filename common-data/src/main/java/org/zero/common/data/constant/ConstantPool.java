package org.zero.common.data.constant;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public interface ConstantPool extends StringPool, CharPool {
    byte BYTE_ZERO = (byte) 0;
    short SHORT_ZERO = (short) 0;
    int INT_ZERO = 0;
    long LONG_ZERO = 0L;
    float FLOAT_ZERO = 0.0F;
    double DOUBLE_ZERO = 0.0D;
    char CHAR_ZERO = '\u0000';
    boolean BOOLEAN_FALSE = false;
    boolean BOOLEAN_TRUE = true;
    Object OBJECT_NULL = null;
}
