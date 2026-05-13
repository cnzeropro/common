package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuadConsumerTest {
	@Test
	void shouldRunConsumersInOrder() {
		StringBuilder builder = new StringBuilder();
		QuadConsumer<String, String, String, String> consumer =
				(a, b, c, d) -> builder.append(a).append(b).append(c).append(d);

		consumer.andThen((a, b, c, d) -> builder.append('|').append(d).append(c).append(b).append(a))
				.accept("1", "2", "3", "4");

		assertEquals("1234|4321", builder.toString());
	}

	@Test
	void shouldNotRunAfterConsumerWhenCurrentConsumerFails() {
		RuntimeException exception = new RuntimeException("failed");
		StringBuilder builder = new StringBuilder();
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {
			throw exception;
		};

		RuntimeException actual = assertThrows(
				RuntimeException.class,
				() -> consumer.andThen((a, b, c, d) -> builder.append("after")).accept("1", "2", "3", "4")
		);

		assertEquals(exception, actual);
		assertEquals("", builder.toString());
	}

	@Test
	void shouldRejectNullAfterConsumer() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {
		};

		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}
