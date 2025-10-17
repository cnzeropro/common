package org.zero.common.core.support.bean.map;

import org.zero.common.data.constant.StringPool;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class ObjectConfig {
	public static final String DEFAULT_PREFIX = StringPool.DOLLAR;

	protected Object object;
	protected String prefix = DEFAULT_PREFIX;

	protected String levelSeparator = StringPool.DOT;
	protected String multivariableLeft = StringPool.SQUARE_LEFT;
	protected String multivariableRight = StringPool.SQUARE_RIGHT;

	protected ObjectConfig(Object object) {
		this.object = object;
	}

	public static ObjectConfig of(Object object) {
		return new ObjectConfig(object);
	}

	public ObjectConfig prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public ObjectConfig levelSeparator(String levelSeparator) {
		this.levelSeparator = levelSeparator;
		return this;
	}

	public ObjectConfig multivariable(String multivariableLeft, String multivariableRight) {
		this.multivariableLeft = multivariableLeft;
		this.multivariableRight = multivariableRight;
		return this;
	}
}
