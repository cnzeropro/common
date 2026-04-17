package org.zero.common.core.extension.java.lang.reflect;

import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2023/7/13
 */
class TypeReferenceTest {

	@Test
	void shouldCaptureComplexGenericTypeFromAnonymousSubclass() throws NoSuchFieldException {
		Type type1 = new TypeReference<Map<List<Map<Long, Date>>[], Set<String>[]>>() {
		}.getType();
		Type type2 = new TypeReference<Map<Queue<Short>, String>[]>() {
		}.getType();

		assertEquals(SampleHolder.class.getDeclaredField("complexMap").getGenericType(), type1);
		assertEquals(SampleHolder.class.getDeclaredField("queueMapArray").getGenericType(), type2);
	}

	@Test
	void shouldWorkWithResolvableTypeAndKeepToStringStable() throws NoSuchFieldException {
		TypeReference<List<Map<String, Set<BigDecimal>>>> reference = new TypeReference<List<Map<String, Set<BigDecimal>>>>() {
		};
		Type expectedType = SampleHolder.class.getDeclaredField("decimalList").getGenericType();
		ResolvableType resolvableType = ResolvableType.forType(reference.getType());

		assertEquals(expectedType, reference.getType());
		assertEquals(expectedType.toString(), reference.toString());
		assertEquals(List.class, resolvableType.getRawClass());
		assertEquals(Map.class, resolvableType.getGeneric(0).getRawClass());
		assertEquals(String.class, resolvableType.getGeneric(0, 0).resolve());
		assertEquals(Set.class, resolvableType.getGeneric(0, 1).getRawClass());
		assertEquals(BigDecimal.class, resolvableType.getGeneric(0, 1, 0).resolve());
	}

	@Test
	void shouldResolveParameterizedSuperclassArguments() {
		Type[] typeArguments = TypeReference.getTypeArguments(SuperTypeChild.class);

		assertArrayEquals(new Type[]{String.class, Integer.class}, typeArguments);
		assertEquals(String.class, TypeReference.getTypeArgument(SuperTypeChild.class));
		assertEquals(Integer.class, TypeReference.getTypeArgument(SuperTypeChild.class, 1));
		assertNull(TypeReference.getTypeArgument(SuperTypeChild.class, 2));
		assertEquals(SuperTypeChild.class.getGenericSuperclass(), TypeReference.getParameterizedType(SuperTypeChild.class));
	}

	@Test
	void shouldResolveParameterizedInterfaceWhenSuperclassIsObject() {
		Type[] typeArguments = TypeReference.getTypeArguments(InterfaceOnlyImpl.class);

		assertArrayEquals(new Type[]{Long.class}, typeArguments);
		assertEquals(Long.class, TypeReference.getTypeArgument(InterfaceOnlyImpl.class));
		assertEquals(InterfaceOnlyImpl.class.getGenericInterfaces()[0], TypeReference.getParameterizedType(InterfaceOnlyImpl.class));
	}

	@Test
	void shouldReturnEmptyResultsForNonParameterizedType() {
		assertArrayEquals(new Type[0], TypeReference.getTypeArguments(String.class));
		assertNull(TypeReference.getTypeArgument(String.class));
		assertNull(TypeReference.getParameterizedType(String.class));
	}

	private interface GenericContract<T> {
	}

	private static class SampleHolder {
		private Map<List<Map<Long, Date>>[], Set<String>[]> complexMap;
		private Map<Queue<Short>, String>[] queueMapArray;
		private List<Map<String, Set<BigDecimal>>> decimalList;
	}

	private static class SuperTypeParent<T, U> {
	}

	private static class SuperTypeChild extends SuperTypeParent<String, Integer> {
	}

	private static class InterfaceOnlyImpl implements GenericContract<Long> {
	}
}
