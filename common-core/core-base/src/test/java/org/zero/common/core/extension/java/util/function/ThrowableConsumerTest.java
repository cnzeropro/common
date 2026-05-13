package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowableConsumerTest {
	@Test
	void shouldWrapStandardConsumer() throws Throwable {
		AtomicReference<String> value = new AtomicReference<>();

		ThrowableConsumer.of(value::set).accept("zero");

		assertEquals("zero", value.get());
	}

	@Test
	void shouldAdaptToConsumerAndAcceptValue() {
		AtomicReference<String> value = new AtomicReference<>();
		ThrowableConsumer<String> throwableConsumer = value::set;

		throwableConsumer.to().accept("zero");

		assertEquals("zero", value.get());
	}

	@Test
	void shouldAdaptToConsumerAndPropagateSameThrowable() {
		IOException exception = new IOException("consumer failed");
		ThrowableConsumer<String> throwableConsumer = value -> {
			throw exception;
		};
		Consumer<String> consumer = throwableConsumer.to();

		IOException actual = assertThrows(IOException.class, () -> consumer.accept("zero"));

		assertSame(exception, actual);
	}
}
