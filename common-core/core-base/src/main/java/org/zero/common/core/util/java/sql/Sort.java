package org.zero.common.core.util.java.sql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/19
 */
@Getter
@RequiredArgsConstructor
public enum Sort {
	ASCENDING("A"),
	DESCENDING("D");

	private final String value;

	public static Sort from(String value) {
		for (Sort type : Sort.values()) {
			if (Objects.equals(type.value, value)) {
				return type;
			}
		}
		return null;
	}
}
