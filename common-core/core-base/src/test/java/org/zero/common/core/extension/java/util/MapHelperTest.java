package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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
		map.put("type", String.class);
		return map;
	}

	@Test
	void shouldExposeMapMetadataAndRawValueMethods() {
		Map<String, Object> map = createMap();
		MapHelper helper = MapHelper.of(map);

		assertSame(map, helper.getMap());
		assertEquals(13, helper.size());
		assertFalse(helper.isEmpty());
		assertTrue(helper.nonEmpty());
		assertTrue(helper.containsKey("name"));
		assertTrue(helper.containsValue("zero"));
		assertTrue(helper.keySet().contains("count"));
		assertTrue(helper.values().contains(3));
		assertEquals(13, helper.entrySet().size());
		assertEquals("zero", helper.get("name"));
		assertTrue(helper.getOpt("name").isPresent());
		assertFalse(helper.getOpt("missing").isPresent());
		assertEquals("fallback", helper.getOrDefault("missing", "fallback"));
		assertNull(helper.getOrDefault("rawNull", "fallback"));
	}

	@Test
	void shouldReadStringAndClassValues() {
		MapHelper helper = MapHelper.of(createMap());

		assertTrue(helper.getStringOpt("name").isPresent());
		assertEquals("zero", helper.getString("name"));
		assertEquals("fallback", helper.getStringOrDefault("missing", "fallback"));
		assertEquals("fallback", helper.getStringOrDefault("missing", () -> "fallback"));
		assertEquals("zero", helper.getRequiredString("name"));
		assertThrows(NoSuchElementException.class, () -> helper.getRequiredString("missing"));

		Class<String> type = helper.getClass("type");
		assertSame(String.class, type);
		assertTrue(helper.getClassOpt("type").isPresent());
		assertNull(helper.getClass("name"));
		assertSame(Integer.class, helper.getClassOrDefault("missing", Integer.class));
		assertSame(Long.class, helper.getClassOrDefault("missing", () -> Long.class));
		assertSame(String.class, helper.getRequiredClass("type"));
		assertThrows(NoSuchElementException.class, () -> helper.getRequiredClass("missing"));
	}

	@Test
	void shouldReadNumberValues() {
		MapHelper helper = MapHelper.of(createMap());

		assertEquals(Byte.valueOf((byte) 1), helper.getByteOpt("byte").orElse(null));
		assertEquals(Byte.valueOf((byte) 1), helper.getBoxedByte("byte"));
		assertEquals((byte) 1, helper.getByte("byte"));
		assertEquals((byte) 9, helper.getByteOrDefault("missing", (byte) 9));
		assertEquals((byte) 9, helper.getByteOrDefault("missing", () -> (byte) 9));
		assertEquals((byte) 1, helper.getRequiredByte("byte"));

		assertEquals(Short.valueOf((short) 2), helper.getShortOpt("short").orElse(null));
		assertEquals(Short.valueOf((short) 2), helper.getBoxedShort("short"));
		assertEquals((short) 2, helper.getShort("short"));
		assertEquals((short) 9, helper.getShortOrDefault("missing", (short) 9));
		assertEquals((short) 9, helper.getShortOrDefault("missing", () -> (short) 9));
		assertEquals((short) 2, helper.getRequiredShort("short"));

		assertEquals(Integer.valueOf(3), helper.getIntOpt("count").orElse(null));
		assertEquals(Integer.valueOf(3), helper.getBoxedInt("count"));
		assertEquals(3, helper.getInt("count"));
		assertEquals(9, helper.getIntOrDefault("missing", 9));
		assertEquals(9, helper.getIntOrDefault("missing", () -> 9));
		assertEquals(3, helper.getRequiredInt("count"));

		assertEquals(Long.valueOf(4L), helper.getLongOpt("long").orElse(null));
		assertEquals(Long.valueOf(4L), helper.getBoxedLong("long"));
		assertEquals(4L, helper.getLong("long"));
		assertEquals(9L, helper.getLongOrDefault("missing", 9L));
		assertEquals(9L, helper.getLongOrDefault("missing", () -> 9L));
		assertEquals(4L, helper.getRequiredLong("long"));

		assertEquals(Float.valueOf(5.5F), helper.getFloatOpt("float").orElse(null));
		assertEquals(Float.valueOf(5.5F), helper.getBoxedFloat("float"));
		assertEquals(5.5F, helper.getFloat("float"));
		assertEquals(9.5F, helper.getFloatOrDefault("missing", 9.5F));
		assertEquals(9.5F, helper.getFloatOrDefault("missing", () -> 9.5F));
		assertEquals(5.5F, helper.getRequiredFloat("float"));

		assertEquals(Double.valueOf(6.5D), helper.getDoubleOpt("double").orElse(null));
		assertEquals(Double.valueOf(6.5D), helper.getBoxedDouble("double"));
		assertEquals(6.5D, helper.getDouble("double"));
		assertEquals(9.5D, helper.getDoubleOrDefault("missing", 9.5D));
		assertEquals(9.5D, helper.getDoubleOrDefault("missing", () -> 9.5D));
		assertEquals(6.5D, helper.getRequiredDouble("double"));
	}

	@Test
	void shouldReadCharBooleanAndLargeNumberValues() {
		MapHelper helper = MapHelper.of(createMap());

		assertEquals(Character.valueOf('Z'), helper.getCharOpt("char").orElse(null));
		assertEquals(Character.valueOf('Z'), helper.getBoxedChar("char"));
		assertEquals('Z', helper.getChar("char"));
		assertEquals('F', helper.getCharOrDefault("missing", 'F'));
		assertEquals('F', helper.getCharOrDefault("missing", () -> 'F'));
		assertEquals('Z', helper.getRequiredChar("char"));

		assertEquals(Boolean.TRUE, helper.getBooleanOpt("enabled").orElse(null));
		assertEquals(Boolean.TRUE, helper.getBoxedBoolean("enabled"));
		assertTrue(helper.getBoolean("enabled"));
		assertFalse(helper.getBoolean("missing"));
		assertTrue(helper.getBooleanOrDefault("missing", true));
		assertTrue(helper.getBooleanOrDefault("missing", () -> true));
		assertTrue(helper.getRequiredBoolean("enabled"));

		assertEquals(new BigDecimal("7.5"), helper.getBigDecimalOpt("bigDecimal").orElse(null));
		assertEquals(new BigDecimal("7.5"), helper.getBigDecimal("bigDecimal"));
		assertEquals(new BigDecimal("9.5"), helper.getBigDecimalOrDefault("missing", new BigDecimal("9.5")));
		assertEquals(new BigDecimal("9.5"), helper.getBigDecimalOrDefault("missing", () -> new BigDecimal("9.5")));
		assertEquals(new BigDecimal("7.5"), helper.getRequiredBigDecimal("bigDecimal"));

		assertEquals(new BigInteger("8"), helper.getBigIntegerOpt("bigInteger").orElse(null));
		assertEquals(new BigInteger("8"), helper.getBigInteger("bigInteger"));
		assertEquals(new BigInteger("9"), helper.getBigIntegerOrDefault("missing", new BigInteger("9")));
		assertEquals(new BigInteger("9"), helper.getBigIntegerOrDefault("missing", () -> new BigInteger("9")));
		assertEquals(new BigInteger("8"), helper.getRequiredBigInteger("bigInteger"));
	}

	@Test
	void shouldReadGenericTypedValues() {
		MapHelper helper = MapHelper.of(createMap());

		assertEquals("zero", helper.get("name", String.class));
		assertNull(helper.get("name", Integer.class));
		assertTrue(helper.getOpt("name", String.class).isPresent());
		assertFalse(helper.getOpt("name", Integer.class).isPresent());
		assertEquals("fallback", helper.getOrDefault("count", String.class, "fallback"));
		assertEquals("zero", helper.getRequired("name", String.class));
		assertThrows(NoSuchElementException.class, () -> helper.getRequired("name", Integer.class));
	}

	@Test
	void shouldUseDefaultSupplierLazily() {
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
	}

	@Test
	void shouldUseDefaultWhenValueMissingNullOrTypeMismatch() {
		MapHelper helper = MapHelper.of(createMap());

		assertNull(helper.getBoxedInt("missing"));
		assertNull(helper.getBoxedInt("rawNull"));
		assertEquals(0, helper.getInt("missing"));
		assertEquals(0, helper.getInt("name"));
		assertEquals(9, helper.getIntOrDefault("name", 9));
		assertEquals(9, helper.getIntOrDefault("name", () -> 9));
		assertFalse(helper.getIntOpt("name").isPresent());
		assertThrows(NoSuchElementException.class, () -> helper.getRequiredInt("name"));
	}

	@Test
	void shouldRejectNullRequiredArgumentsWhenNeeded() {
		MapHelper helper = MapHelper.of(createMap());

		assertThrows(NullPointerException.class, () -> helper.get("name", null));
		assertThrows(NullPointerException.class, () -> helper.getOpt("name", null));
		assertThrows(NullPointerException.class, () -> helper.getRequired("name", null));
		assertThrows(NullPointerException.class, () -> helper.getOrDefault("name", null, "fallback"));
		assertThrows(NullPointerException.class, () -> helper.getStringOrDefault("missing", (Supplier<String>) null));
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
