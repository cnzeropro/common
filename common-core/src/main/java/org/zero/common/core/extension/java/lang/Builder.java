package org.zero.common.core.extension.java.lang;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
public interface Builder<T, B extends Builder<T, B>> extends Serializable {
	T build();

	@SuppressWarnings("unchecked")
	default B self() {
		return (B) this;
	}
}
