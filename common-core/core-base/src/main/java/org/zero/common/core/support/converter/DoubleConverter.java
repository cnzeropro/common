package org.zero.common.core.support.converter;

import org.zero.common.data.constant.ConstantPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class DoubleConverter implements Converter {
	public static final DoubleConverter INSTANCE = new DoubleConverter();

	public double convert(Object source) {
		Double d = ToDouble.INSTANCE.convert(source);
		return Objects.nonNull(d) ? d : ConstantPool.DOUBLE_ZERO;
	}
}
