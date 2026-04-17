package org.zero.common.core.extension.java.lang.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class GenericArrayTypeImplTest {

	@Test
	void shouldMatchJdkGenericArrayTypeForParameterizedComponent() throws NoSuchFieldException {
		ParameterizedType componentType = ParameterizedTypeImpl.make(List.class, String.class);
		GenericArrayTypeImpl actualType = GenericArrayTypeImpl.make(componentType);
		GenericArrayType expectedType = (GenericArrayType) SampleHolder.class.getDeclaredField("listArray").getGenericType();

		assertEquals(expectedType, actualType);
		assertEquals(expectedType.hashCode(), actualType.hashCode());
		assertEquals(expectedType.toString(), actualType.toString());
	}

	@Test
	void shouldUseComponentTypeForEqualityAndStringRendering() {
		GenericArrayTypeImpl stringArrayType = GenericArrayTypeImpl.make(String.class);
		GenericArrayTypeImpl sameType = GenericArrayTypeImpl.make(String.class);
		GenericArrayTypeImpl integerArrayType = GenericArrayTypeImpl.make(Integer.class);

		assertSame(String.class, stringArrayType.getGenericComponentType());
		assertEquals(sameType, stringArrayType);
		assertNotEquals(integerArrayType, stringArrayType);
		assertEquals("java.lang.String[]", stringArrayType.toString());
	}

	private static class SampleHolder {
		private List<String>[] listArray;
	}
}
