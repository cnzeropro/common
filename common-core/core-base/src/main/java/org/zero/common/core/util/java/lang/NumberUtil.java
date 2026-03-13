package org.zero.common.core.util.java.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class NumberUtil {
	public static final BigInteger BIG_INTEGER_MIN_VALUE_INT = BigInteger.valueOf(Integer.MIN_VALUE);
	public static final BigInteger BIG_INTEGER_MIN_VALUE_LONG = BigInteger.valueOf(Long.MIN_VALUE);
	public static final BigInteger BIG_INTEGER_MAX_VALUE_INT = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger BIG_INTEGER_MAX_VALUE_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	public static final BigDecimal BIG_DECIMAL_MIN_VALUE_FLOAT = BigDecimal.valueOf(Float.MIN_VALUE);
	public static final BigDecimal BIG_DECIMAL_MIN_VALUE_DOUBLE = BigDecimal.valueOf(Double.MIN_VALUE);
	public static final BigDecimal BIG_DECIMAL_MAX_VALUE_FLOAT = BigDecimal.valueOf(Float.MAX_VALUE);
	public static final BigDecimal BIG_DECIMAL_MAX_VALUE_DOUBLE = BigDecimal.valueOf(Double.MAX_VALUE);

	public static int toInt(BigInteger value) {
		if (Objects.isNull(value)) {
			return 0;
		}
		if (value.compareTo(BIG_INTEGER_MIN_VALUE_INT) <= 0) {
			return Integer.MIN_VALUE;
		}
		if (value.compareTo(BIG_INTEGER_MAX_VALUE_INT) >= 0) {
			return Integer.MAX_VALUE;
		}
		return value.intValue();
	}

	public static long toLong(BigInteger value) {
		if (Objects.isNull(value)) {
			return 0L;
		}
		if (value.compareTo(BIG_INTEGER_MIN_VALUE_LONG) <= 0) {
			return Long.MIN_VALUE;
		}
		if (value.compareTo(BIG_INTEGER_MAX_VALUE_LONG) >= 0) {
			return Long.MAX_VALUE;
		}
		return value.longValue();
	}

	public static float toFloat(BigDecimal value) {
		if (Objects.isNull(value)) {
			return 0.0F;
		}
		if (value.compareTo(BIG_DECIMAL_MIN_VALUE_FLOAT) <= 0) {
			return Float.MIN_VALUE;
		}
		if (value.compareTo(BIG_DECIMAL_MAX_VALUE_FLOAT) >= 0) {
			return Float.MAX_VALUE;
		}
		return value.floatValue();
	}

	public static double toDouble(BigDecimal value) {
		if (Objects.isNull(value)) {
			return 0.0D;
		}
		if (value.compareTo(BIG_DECIMAL_MIN_VALUE_DOUBLE) <= 0) {
			return Double.MIN_VALUE;
		}
		if (value.compareTo(BIG_DECIMAL_MAX_VALUE_DOUBLE) >= 0) {
			return Double.MAX_VALUE;
		}
		return value.doubleValue();
	}

	public static double toDouble(final String str, final double defaultValue) {
		if (CharSequenceUtil.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	public static float toFloat(final String str, final float defaultValue) {
		if (CharSequenceUtil.isBlank(str)) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}
}
