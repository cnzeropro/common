package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowableSupplierTest {
	@Test
	void shouldWrapStandardSupplier() throws Throwable {
		assertEquals("zero", ThrowableSupplier.of(() -> "zero").get());
	}

	@Test
	void shouldAdaptToSupplierAndReturnValue() {
		ThrowableSupplier<String> throwableSupplier = () -> "zero";

		assertEquals("zero", throwableSupplier.to().get());
	}

	@Test
	void shouldAdaptToSupplierAndPropagateSameThrowable() {
		IOException exception = new IOException("supplier failed");
		ThrowableSupplier<String> throwableSupplier = () -> {
			throw exception;
		};
		Supplier<String> supplier = throwableSupplier.to();

		IOException actual = assertThrows(IOException.class, supplier::get);

		assertSame(exception, actual);
	}
}
