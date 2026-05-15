package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.zero.common.core.extension.java.util.PropertiesEnhancer;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PropertiesEnhancer} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/4/12
 */
class PropertiesEnhancerTest {
	@Test
	void shouldExposePropertiesMetadata() {
		Properties properties = new Properties();
		properties.setProperty("name", "zero");
		PropertiesEnhancer enhancer = PropertiesEnhancer.init(properties);

		assertSame(properties, enhancer.getProperties());
		assertEquals(1, enhancer.size());
		assertFalse(enhancer.isEmpty());
		assertTrue(enhancer.nonEmpty());
		assertTrue(enhancer.containsKey("name"));
		assertTrue(enhancer.containsValue("zero"));
	}

	@Test
	void shouldReadStringAndRequiredValue() {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.setProperty("name", "zero");
		AtomicBoolean defaultCalled = new AtomicBoolean(false);

		String actual = enhancer.getStringOrDefault("name", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("zero", actual);
		assertFalse(defaultCalled.get());
		assertEquals("zero", enhancer.getString("name"));
		assertEquals("zero", enhancer.getRequiredString("name"));
		assertEquals("fallback", enhancer.getStringOrDefault("missing", "fallback"));
		assertEquals("fallback", enhancer.getStringOrDefault("missing", () -> "fallback"));
		assertEquals("fallback", enhancer.getStringOrDefault("missing", () -> {
			defaultCalled.set(true);
			return "fallback";
		}));
		assertTrue(defaultCalled.get());
		assertThrows(NoSuchElementException.class, () -> enhancer.getRequiredString("missing"));
	}

	@Test
	void shouldReadTypedValues() {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.setProperty("byte", "1");
		enhancer.setProperty("short", "2");
		enhancer.setProperty("int", "3");
		enhancer.setProperty("long", "4");
		enhancer.setProperty("float", "5.5");
		enhancer.setProperty("double", "6.5");
		enhancer.setProperty("char", "Z");
		enhancer.setProperty("boolean", "true");
		enhancer.setProperty("bigDecimal", "7.5");
		enhancer.setProperty("bigInteger", "8");

		assertEquals((byte) 1, enhancer.getByte("byte"));
		assertEquals(Byte.valueOf((byte) 1), enhancer.getBoxedByte("byte"));
		assertEquals((short) 2, enhancer.getShort("short"));
		assertEquals(3, enhancer.getInt("int"));
		assertEquals(Integer.valueOf(3), enhancer.getBoxedInt("int"));
		assertEquals(4L, enhancer.getLong("long"));
		assertEquals(5.5F, enhancer.getFloat("float"));
		assertEquals(6.5D, enhancer.getDouble("double"));
		assertEquals('Z', enhancer.getChar("char"));
		assertTrue(enhancer.getBoolean("boolean"));
		assertEquals(new BigDecimal("7.5"), enhancer.getBigDecimal("bigDecimal"));
		assertEquals(new BigInteger("8"), enhancer.getBigInteger("bigInteger"));
		assertNull(enhancer.getBoxedInt("missing"));
		assertEquals(9, enhancer.getIntOrDefault("missing", 9));
		assertEquals(9, enhancer.getIntOrDefault("missing", () -> 9));
		assertEquals(3, enhancer.getRequiredInt("int"));
		assertEquals(new BigInteger("8"), enhancer.getRequiredBigInteger("bigInteger"));
	}

	@Test
	void shouldReadClassValue() {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.setProperty("type", String.class.getName());

		Class<String> type = enhancer.getClass("type");

		assertSame(String.class, type);
		assertTrue(enhancer.getClassOpt("type").isPresent());
		assertSame(Integer.class, enhancer.getClassOrDefault("missing", Integer.class));
		assertSame(Long.class, enhancer.getClassOrDefault("missing", () -> Long.class));
		assertSame(String.class, enhancer.getRequiredClass("type"));
	}

	@Test
	void shouldLoadPropertiesFile(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("app.properties");
		Files.write(file, Collections.singletonList("name=zero"), StandardCharsets.UTF_8);

		PropertiesEnhancer enhancer = PropertiesEnhancer.init(file);

		assertEquals("zero", enhancer.getString("name"));
	}

	@Test
	void shouldLoadXmlFileIgnoringSuffixCase(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("app.XML");
		Properties properties = new Properties();
		properties.setProperty("name", "zero");
		try (OutputStream outputStream = Files.newOutputStream(file)) {
			properties.storeToXML(outputStream, null);
		}

		PropertiesEnhancer enhancer = PropertiesEnhancer.init(file);

		assertEquals("zero", enhancer.getString("name"));
	}

	@Test
	void shouldRejectNullInput() {
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((Properties) null));
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((String) null));
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((Path) null));
	}
}
