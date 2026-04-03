package org.zero.common.core.support.query;

import java.util.Locale;

/**
 * 排序方向。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public enum SortDirection {
	ASC,
	DESC;

	public static SortDirection fromCode(String code) {
		if (code == null || code.trim().isEmpty()) {
			return ASC;
		}
		String normalizedCode = code.trim().toUpperCase(Locale.ENGLISH);
		for (SortDirection direction : values()) {
			if (direction.name().equals(normalizedCode)) {
				return direction;
			}
		}
		throw new IllegalArgumentException(String.format("Unsupported sort direction: %s", code));
	}
}
