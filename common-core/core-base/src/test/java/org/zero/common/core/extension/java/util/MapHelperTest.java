package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MapHelper} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class MapHelperTest {
	private static Map<String, Object> createMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", "zero");
		map.put("byte", (byte) 1);
		map.put("short", (short) 2);
		map.put("count", 3);
		map.put("long", 4L);
		map.put("float", 5.5F);
		map.put("double", 6.5D);
		map.put("char", 'Z');
		map.put("enabled", true);
		map.put("bigDecimal", new BigDecimal("7.5"));
		map.put("bigInteger", new BigInteger("8"));
		map.put("rawNull", null);
		return map;
	}

	@Test
	void shouldExposeMapMetadata() {
		Map<String, Object> map = createMap();
		MapHelper helper = MapHelper.of(map);

		assertSame(map, helper.getMap());
		assertEquals(12, helper.size());
		assertFalse(helper.isEmpty());
		assertTrue(helper.nonEmpty());
		assertTrue(helper.containsKey("name"));
		assertTrue(helper.containsValue("zero"));
		assertTrue(helper.keySet().contains("count"));
		assertTrue(helper.values().contains(3));
		assertEquals(12, helper.entrySet().size());
	}

	@Test
	void shouldReadTypedValueAndDefaultValue() {
		MapHelper helper = MapHelper.of(createMap());

		assertTrue(helper.getStringOpt("name").isPresent());
		assertEquals("zero", helper.getString("name"));
		assertEquals("zero", helper.getRequiredString("name"));
		assertEquals("fallback", helper.getStringOrDefault("missing", "fallback"));
		assertEquals("fallback", helper.getStringOrDefault("missing", () -> "fallback"));
		assertEquals((byte) 1, helper.getByte("byte"));
		assertEquals(Byte.valueOf((byte) 1), helper.getBoxedByte("byte"));
		assertEquals((short) 2, helper.getShort("short"));
		assertEquals(3, helper.getInt("count"));
		assertEquals(Integer.valueOf(3), helper.getBoxedInt("count"));
		assertEquals(4L, helper.getLong("long"));
		assertEquals(5.5F, helper.getFloat("float"));
		assertEquals(6.5D, helper.getDouble("double"));
		assertEquals('Z', helper.getChar("char"));
		assertEquals(9, helper.getIntOrDefault("missing", 9));
		assertTrue(helper.getBoolean("enabled"));
		assertFalse(helper.getBoolean("missing"));
		assertEquals(new BigDecimal("7.5"), helper.getBigDecimal("bigDecimal"));
		assertEquals(new BigInteger("8"), helper.getBigInteger("bigInteger"));
		assertEquals("fallback", helper.getOrDefault("count", String.class, "fallback"));
		assertNull(helper.getOrDefault("rawNull", "fallback"));
	}

	@Test
	void shouldReadDefaultValueLazily() {
		MapHelper helper = MapHelper.of(createMap());
		AtomicBoolean defaultCalled = new AtomicBoolean(false);

		String actual = helper.getStringOrDefault("name", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("zero", actual);
		assertFalse(defaultCalled.get());

		String fallback = helper.getStringOrDefault("missing", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("fallback", fallback);
		assertTrue(defaultCalled.get());
		assertEquals(9, helper.getIntOrDefault("missing", () -> 9));
		assertEquals(new BigDecimal("9.5"), helper.getBigDecimalOrDefault("missing", () -> new BigDecimal("9.5")));
	}

	@Test
	void shouldReadClassValue() {
		Map<String, Object> map = createMap();
		map.put("type", String.class);
		MapHelper helper = MapHelper.of(map);

		Class<String> type = helper.getClass("type");

		assertSame(String.class, type);
		assertTrue(helper.getClassOpt("type").isPresent());
		assertSame(Integer.class, helper.getClassOrDefault("missing", Integer.class));
		assertSame(Long.class, helper.getClassOrDefault("missing", () -> Long.class));
		assertSame(String.class, helper.getRequiredClass("type"));
	}

	@Test
	void shouldThrowWhenRequiredValueMissingOrTypeMismatch() {
		MapHelper helper = MapHelper.of(createMap());

		assertEquals("zero", helper.getRequired("name", String.class));
		assertEquals(3, helper.getRequiredInt("count"));
		assertEquals(new BigInteger("8"), helper.getRequiredBigInteger("bigInteger"));
		assertThrows(NoSuchElementException.class, () -> helper.getRequired("missing"));
		assertThrows(NoSuchElementException.class, () -> helper.getRequired("name", Integer.class));
		assertThrows(NoSuchElementException.class, () -> helper.getRequiredString("missing"));
	}

	@Test
	void shouldProvideSharedEmptyHelper() {
		MapHelper helper = MapHelper.empty();

		assertSame(helper, MapHelper.empty());
		assertTrue(helper.isEmpty());
		assertFalse(helper.nonEmpty());
		assertNull(helper.get("missing"));
		assertEquals("fallback", helper.getStringOrDefault("missing", "fallback"));
	}

	@Test
	void shouldRejectNullMap() {
		assertThrows(NullPointerException.class, () -> new MapHelper(null));
	}
}
