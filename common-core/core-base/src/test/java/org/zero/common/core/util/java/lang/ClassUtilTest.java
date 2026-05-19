package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/8 16:54
 */
class ClassUtilTest {
	@Test
	void getClassMap() {
		Map<String, Class<?>> classMap = ClassUtil.getClassMap("org.zero.common.data.model");
		classMap.forEach((k, v) -> System.out.println(k + ":" + v));
	}

	@Test
	void getClassNames() {
		Collection<String> classNames = ClassUtil.getClassNames("org.zero.common");
		classNames.forEach(System.out::println);
	}

	@Test
	void isConvertible() {
		System.out.println(ClassUtil.isConvertible(Number.class, Integer.class));
		System.out.println(ClassUtil.isConvertible(Integer.class, int.class));
		System.out.println(ClassUtil.isConvertible(int.class, Integer.class));
		System.out.println(ClassUtil.isConvertible(Number[].class, Integer[].class));
		System.out.println(ClassUtil.isConvertible(Integer[].class, int[].class));
		System.out.println(ClassUtil.isConvertible(Integer.class, Double.class));
	}
}
