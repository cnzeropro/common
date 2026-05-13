package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class TriConsumerTest {
	@Test
	void shouldRunConsumersInOrder() {
		StringBuilder builder = new StringBuilder();
		TriConsumer<String, String, String> consumer = (a, b, c) -> builder.append(a).append(b).append(c);

		consumer.andThen((a, b, c) -> builder.append('|').append(c).append(b).append(a)).accept("A", "B", "C");

		assertEquals("ABC|CBA", builder.toString());
	}

	@Test
	void shouldNotRunAfterConsumerWhenCurrentConsumerFails() {
		RuntimeException exception = new RuntimeException("failed");
		StringBuilder builder = new StringBuilder();
		TriConsumer<String, String, String> consumer = (a, b, c) -> {
			throw exception;
		};

		RuntimeException actual = assertThrows(
				RuntimeException.class,
				() -> consumer.andThen((a, b, c) -> builder.append("after")).accept("A", "B", "C")
		);

		assertEquals(exception, actual);
		assertEquals("", builder.toString());
	}

	@Test
	void shouldRejectNullAfterConsumer() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {
		};

		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}
