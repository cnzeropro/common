package org.zero.common.core.support.bean.map;

import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * 默认的直接评估器
 *
 * @author Zero (cnzeropro@163.com)
 * @see BeanUtils#isSimpleValueType(Class)
 * @since 2025/10/15
 */
public class DefaultDirectEvaluator implements DirectEvaluator {
	public static final DefaultDirectEvaluator INSTANCE = new DefaultDirectEvaluator();

	@Override
	public boolean evaluate(Object object) {
		if (Objects.isNull(object)) {
			return false;
		}
		return BeanUtils.isSimpleValueType(object.getClass());
	}
}
