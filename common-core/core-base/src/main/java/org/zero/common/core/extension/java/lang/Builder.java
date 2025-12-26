package org.zero.common.core.extension.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
public interface Builder<T, B extends Builder<T, B>> {
	T build();
}
