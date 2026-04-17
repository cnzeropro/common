package org.zero.common.core.extension.java.lang.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class ParameterizedTypeImplTest {

	@Test
	void shouldMatchJdkParameterizedTypeForNestedOwner() throws NoSuchFieldException {
		ParameterizedType ownerType = ParameterizedTypeImpl.make(GenericOuter.class, String.class);
		ParameterizedTypeImpl actualType = ParameterizedTypeImpl.make(
				GenericOuter.GenericInner.class,
				new Type[]{Integer.class},
				ownerType
		);
		ParameterizedType expectedType = (ParameterizedType) SampleHolder.class.getDeclaredField("inner").getGenericType();

		assertEquals(expectedType, actualType);
		assertEquals(expectedType.hashCode(), actualType.hashCode());
		assertEquals(expectedType.toString(), actualType.toString());
	}

	@Test
	void shouldInferDeclaringClassWhenOwnerTypeIsNull() {
		ParameterizedTypeImpl type = assertDoesNotThrow(() -> ParameterizedTypeImpl.make(GenericOuter.GenericInner.class, Integer.class));

		assertEquals(GenericOuter.class, type.getOwnerType());
		assertEquals(GenericOuter.class.getName() + "$" + GenericOuter.GenericInner.class.getSimpleName() + "<" + Integer.class.getTypeName() + ">",
				type.toString());
	}

	@Test
	void shouldDefensivelyCopyActualTypeArguments() {
		Type[] source = {String.class};
		ParameterizedTypeImpl type = ParameterizedTypeImpl.make(List.class, source);

		source[0] = Integer.class;
		assertArrayEquals(new Type[]{String.class}, type.getActualTypeArguments());

		Type[] exposed = type.getActualTypeArguments();
		exposed[0] = Long.class;
		assertArrayEquals(new Type[]{String.class}, type.getActualTypeArguments());
	}

	@Test
	void shouldKeepFlatParameterizedTypeBehavior() throws NoSuchFieldException {
		ParameterizedTypeImpl actualType = ParameterizedTypeImpl.make(List.class, String.class);
		ParameterizedType expectedType = (ParameterizedType) SampleHolder.class.getDeclaredField("list").getGenericType();

		assertEquals(expectedType, actualType);
		assertEquals(expectedType.hashCode(), actualType.hashCode());
		assertEquals(expectedType.toString(), actualType.toString());
		assertNull(actualType.getOwnerType());
	}

	private static class GenericOuter<T> {
		class GenericInner<U> {
		}
	}

	private static class SampleHolder {
		private GenericOuter<String>.GenericInner<Integer> inner;
		private List<String> list;
	}
}
