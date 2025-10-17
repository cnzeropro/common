package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/26
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
	MALE(1, "男"),
	FEMALE(2, "女"),
	UNKNOWN(0, "未知");

	private final Integer type;
	private final String name;
}
