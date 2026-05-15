package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static PropertiesEnhancer createEnhancer() {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.setProperty("string", "zero");
		enhancer.setProperty("byte", "1");
		enhancer.setProperty("short", "2");
		enhancer.setProperty("int", "3");
		enhancer.setProperty("long", "4");
		enhancer.setProperty("float", "5.5");
		enhancer.setProperty("double", "6.5");
		enhancer.setProperty("char", "Z");
		enhancer.setProperty("emptyChar", "");
		enhancer.setProperty("boolean", "true");
		enhancer.setProperty("falseText", "anything");
		enhancer.setProperty("bigDecimal", "7.5");
		enhancer.setProperty("bigInteger", "8");
		enhancer.setProperty("class", String.class.getName());
		enhancer.setProperty("missingClass", "no.such.Type");
		return enhancer;
	}

	private static void runWithClassLoaderLogOff(Runnable runnable) {
		Logger logger = Logger.getLogger(ClassLoaderUtil.class.getName());
		Level previousLevel = logger.getLevel();
		logger.setLevel(Level.OFF);
		try {
			runnable.run();
		} finally {
			logger.setLevel(previousLevel);
		}
	}

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
		assertEquals("zero", enhancer.getProperty("name"));
		assertTrue(enhancer.stringPropertyNames().contains("name"));
	}

	@Test
	void shouldReadStringValues() {
		PropertiesEnhancer enhancer = createEnhancer();
		AtomicBoolean defaultCalled = new AtomicBoolean(false);

		String actual = enhancer.getStringOrDefault("string", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("zero", actual);
		assertFalse(defaultCalled.get());
		assertTrue(enhancer.getStringOpt("string").isPresent());
		assertEquals("zero", enhancer.getString("string"));
		assertEquals("zero", enhancer.getRequiredString("string"));
		assertEquals("fallback", enhancer.getStringOrDefault("missing", "fallback"));
		assertEquals("fallback", enhancer.getStringOrDefault("missing", () -> "fallback"));
		assertThrows(NoSuchElementException.class, () -> enhancer.getRequiredString("missing"));
	}

	@Test
	void shouldReadNumberValues() {
		PropertiesEnhancer enhancer = createEnhancer();

		assertEquals(Byte.valueOf((byte) 1), enhancer.getByteOpt("byte").orElse(null));
		assertEquals(Byte.valueOf((byte) 1), enhancer.getBoxedByte("byte"));
		assertEquals((byte) 1, enhancer.getByte("byte"));
		assertEquals((byte) 9, enhancer.getByteOrDefault("missing", (byte) 9));
		assertEquals((byte) 9, enhancer.getByteOrDefault("missing", () -> (byte) 9));
		assertEquals((byte) 1, enhancer.getRequiredByte("byte"));

		assertEquals(Short.valueOf((short) 2), enhancer.getShortOpt("short").orElse(null));
		assertEquals(Short.valueOf((short) 2), enhancer.getBoxedShort("short"));
		assertEquals((short) 2, enhancer.getShort("short"));
		assertEquals((short) 9, enhancer.getShortOrDefault("missing", (short) 9));
		assertEquals((short) 9, enhancer.getShortOrDefault("missing", () -> (short) 9));
		assertEquals((short) 2, enhancer.getRequiredShort("short"));

		assertEquals(Integer.valueOf(3), enhancer.getIntOpt("int").orElse(null));
		assertEquals(Integer.valueOf(3), enhancer.getBoxedInt("int"));
		assertEquals(3, enhancer.getInt("int"));
		assertEquals(9, enhancer.getIntOrDefault("missing", 9));
		assertEquals(9, enhancer.getIntOrDefault("missing", () -> 9));
		assertEquals(3, enhancer.getRequiredInt("int"));

		assertEquals(Long.valueOf(4L), enhancer.getLongOpt("long").orElse(null));
		assertEquals(Long.valueOf(4L), enhancer.getBoxedLong("long"));
		assertEquals(4L, enhancer.getLong("long"));
		assertEquals(9L, enhancer.getLongOrDefault("missing", 9L));
		assertEquals(9L, enhancer.getLongOrDefault("missing", () -> 9L));
		assertEquals(4L, enhancer.getRequiredLong("long"));

		assertEquals(Float.valueOf(5.5F), enhancer.getFloatOpt("float").orElse(null));
		assertEquals(Float.valueOf(5.5F), enhancer.getBoxedFloat("float"));
		assertEquals(5.5F, enhancer.getFloat("float"));
		assertEquals(9.5F, enhancer.getFloatOrDefault("missing", 9.5F));
		assertEquals(9.5F, enhancer.getFloatOrDefault("missing", () -> 9.5F));
		assertEquals(5.5F, enhancer.getRequiredFloat("float"));

		assertEquals(Double.valueOf(6.5D), enhancer.getDoubleOpt("double").orElse(null));
		assertEquals(Double.valueOf(6.5D), enhancer.getBoxedDouble("double"));
		assertEquals(6.5D, enhancer.getDouble("double"));
		assertEquals(9.5D, enhancer.getDoubleOrDefault("missing", 9.5D));
		assertEquals(9.5D, enhancer.getDoubleOrDefault("missing", () -> 9.5D));
		assertEquals(6.5D, enhancer.getRequiredDouble("double"));
	}

	@Test
	void shouldReadCharBooleanAndLargeNumberValues() {
		PropertiesEnhancer enhancer = createEnhancer();

		assertEquals(Character.valueOf('Z'), enhancer.getCharOpt("char").orElse(null));
		assertEquals(Character.valueOf('Z'), enhancer.getBoxedChar("char"));
		assertEquals('Z', enhancer.getChar("char"));
		assertFalse(enhancer.getCharOpt("emptyChar").isPresent());
		assertEquals('F', enhancer.getCharOrDefault("emptyChar", 'F'));
		assertEquals('F', enhancer.getCharOrDefault("missing", () -> 'F'));
		assertEquals('Z', enhancer.getRequiredChar("char"));
		assertThrows(NoSuchElementException.class, () -> enhancer.getRequiredChar("emptyChar"));

		assertEquals(Boolean.TRUE, enhancer.getBooleanOpt("boolean").orElse(null));
		assertEquals(Boolean.TRUE, enhancer.getBoxedBoolean("boolean"));
		assertTrue(enhancer.getBoolean("boolean"));
		assertFalse(enhancer.getBoolean("missing"));
		assertFalse(enhancer.getBoolean("falseText"));
		assertTrue(enhancer.getBooleanOrDefault("missing", true));
		assertTrue(enhancer.getBooleanOrDefault("missing", () -> true));
		assertTrue(enhancer.getRequiredBoolean("boolean"));

		assertEquals(new BigDecimal("7.5"), enhancer.getBigDecimalOpt("bigDecimal").orElse(null));
		assertEquals(new BigDecimal("7.5"), enhancer.getBigDecimal("bigDecimal"));
		assertEquals(new BigDecimal("9.5"), enhancer.getBigDecimalOrDefault("missing", new BigDecimal("9.5")));
		assertEquals(new BigDecimal("9.5"), enhancer.getBigDecimalOrDefault("missing", () -> new BigDecimal("9.5")));
		assertEquals(new BigDecimal("7.5"), enhancer.getRequiredBigDecimal("bigDecimal"));

		assertEquals(new BigInteger("8"), enhancer.getBigIntegerOpt("bigInteger").orElse(null));
		assertEquals(new BigInteger("8"), enhancer.getBigInteger("bigInteger"));
		assertEquals(new BigInteger("9"), enhancer.getBigIntegerOrDefault("missing", new BigInteger("9")));
		assertEquals(new BigInteger("9"), enhancer.getBigIntegerOrDefault("missing", () -> new BigInteger("9")));
		assertEquals(new BigInteger("8"), enhancer.getRequiredBigInteger("bigInteger"));
	}

	@Test
	void shouldReadClassValue() {
		PropertiesEnhancer enhancer = createEnhancer();

		Class<String> type = enhancer.getClass("class");

		assertSame(String.class, type);
		assertTrue(enhancer.getClassOpt("class").isPresent());
		assertSame(Integer.class, enhancer.getClassOrDefault("missing", Integer.class));
		assertSame(Long.class, enhancer.getClassOrDefault("missing", () -> Long.class));
		assertSame(String.class, enhancer.getRequiredClass("class"));
		runWithClassLoaderLogOff(() -> {
			assertFalse(enhancer.getClassOpt("missingClass").isPresent());
			assertNull(enhancer.getClass("missingClass"));
			assertThrows(NoSuchElementException.class, () -> enhancer.getRequiredClass("missingClass"));
		});
	}

	@Test
	void shouldUseDefaultSupplierLazily() {
		PropertiesEnhancer enhancer = createEnhancer();
		AtomicBoolean defaultCalled = new AtomicBoolean(false);

		String actual = enhancer.getStringOrDefault("string", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("zero", actual);
		assertFalse(defaultCalled.get());

		String fallback = enhancer.getStringOrDefault("missing", () -> {
			defaultCalled.set(true);
			return "fallback";
		});

		assertEquals("fallback", fallback);
		assertTrue(defaultCalled.get());
	}

	@Test
	void shouldReturnDefaultsWhenPropertyMissing() {
		PropertiesEnhancer enhancer = createEnhancer();

		assertFalse(enhancer.getIntOpt("missing").isPresent());
		assertNull(enhancer.getBoxedInt("missing"));
		assertEquals(0, enhancer.getInt("missing"));
		assertEquals(9, enhancer.getIntOrDefault("missing", 9));
		assertEquals(9, enhancer.getIntOrDefault("missing", () -> 9));
		assertThrows(NoSuchElementException.class, () -> enhancer.getRequiredInt("missing"));
	}

	@Test
	void shouldKeepParserExceptionsForInvalidValues() {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.setProperty("badNumber", "bad");

		assertThrows(NumberFormatException.class, () -> enhancer.getIntOpt("badNumber"));
		assertThrows(NumberFormatException.class, () -> enhancer.getInt("badNumber"));
		assertThrows(NumberFormatException.class, () -> enhancer.getIntOrDefault("badNumber", 9));
		assertThrows(NumberFormatException.class, () -> enhancer.getRequiredInt("badNumber"));
	}

	@Test
	void shouldRejectNullSupplierWhenDefaultIsRequired() {
		PropertiesEnhancer enhancer = createEnhancer();

		assertThrows(NullPointerException.class, () -> enhancer.getStringOrDefault("missing", (Supplier<String>) null));
		assertThrows(NullPointerException.class, () -> enhancer.getIntOrDefault("missing", (Supplier<Integer>) null));
	}

	@Test
	void shouldLoadPropertiesFile(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("app.properties");
		Files.write(file, Collections.singletonList("string=zero"), StandardCharsets.UTF_8);

		PropertiesEnhancer pathEnhancer = PropertiesEnhancer.init(file);
		PropertiesEnhancer stringEnhancer = PropertiesEnhancer.init(file.toString());

		assertEquals("zero", pathEnhancer.getString("string"));
		assertEquals("zero", stringEnhancer.getString("string"));
	}

	@Test
	void shouldLoadXmlFileIgnoringSuffixCase(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("app.XML");
		Properties properties = new Properties();
		properties.setProperty("string", "zero");
		try (OutputStream outputStream = Files.newOutputStream(file)) {
			properties.storeToXML(outputStream, null);
		}

		PropertiesEnhancer enhancer = PropertiesEnhancer.init(file);

		assertEquals("zero", enhancer.getString("string"));
	}

	@Test
	void shouldDelegateLoadStoreAndListMethods() throws Exception {
		PropertiesEnhancer enhancer = PropertiesEnhancer.init();
		enhancer.load(new StringReader("string=zero\nint=3\n"));
		enhancer.load(new ByteArrayInputStream("stream=loaded\n".getBytes(StandardCharsets.ISO_8859_1)));

		assertEquals("zero", enhancer.getString("string"));
		assertEquals(3, enhancer.getInt("int"));
		assertEquals("loaded", enhancer.getString("stream"));

		StringWriter writer = new StringWriter();
		enhancer.store(writer, "comments");
		assertTrue(writer.toString().contains("string=zero"));

		ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
		enhancer.storeToXML(xmlOutputStream, "comments", StandardCharsets.UTF_8.name());
		PropertiesEnhancer xmlEnhancer = PropertiesEnhancer.init();
		xmlEnhancer.loadFromXML(new ByteArrayInputStream(xmlOutputStream.toByteArray()));
		assertEquals("zero", xmlEnhancer.getString("string"));

		StringWriter listWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(listWriter);
		enhancer.list(printWriter);
		printWriter.flush();
		assertTrue(listWriter.toString().contains("string=zero"));

		ByteArrayOutputStream listOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(listOutputStream, true, StandardCharsets.UTF_8.name());
		enhancer.list(printStream);
		assertTrue(listOutputStream.toString(StandardCharsets.UTF_8.name()).contains("string=zero"));
	}

	@Test
	void shouldRejectNullInput() {
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((Properties) null));
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((String) null));
		assertThrows(NullPointerException.class, () -> PropertiesEnhancer.init((Path) null));
	}
}
