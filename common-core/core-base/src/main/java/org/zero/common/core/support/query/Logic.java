package org.zero.common.core.support.query;

import java.util.Locale;

/**
 * 条件逻辑。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public enum Logic {
	AND,
	OR;

	public static Logic fromCode(String code) {
		if (code == null || code.trim().isEmpty()) {
			return AND;
		}
		String normalizedCode = code.trim().toUpperCase(Locale.ENGLISH);
		for (Logic logic : values()) {
			if (logic.name().equals(normalizedCode)) {
				return logic;
			}
		}
		throw new IllegalArgumentException(String.format("Unsupported logic: %s", code));
	}
}
