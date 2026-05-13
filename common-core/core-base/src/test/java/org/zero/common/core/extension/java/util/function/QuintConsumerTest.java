package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuintConsumerTest {
	@Test
	void shouldRunConsumersInOrder() {
		StringBuilder builder = new StringBuilder();
		QuintConsumer<String, String, String, String, String> consumer =
				(a, b, c, d, e) -> builder.append(a).append(b).append(c).append(d).append(e);

		consumer.andThen((a, b, c, d, e) -> builder.append('|').append(e).append(d).append(c).append(b).append(a))
				.accept("v", "w", "x", "y", "z");

		assertEquals("vwxyz|zyxwv", builder.toString());
	}

	@Test
	void shouldNotRunAfterConsumerWhenCurrentConsumerFails() {
		RuntimeException exception = new RuntimeException("failed");
		StringBuilder builder = new StringBuilder();
		QuintConsumer<String, String, String, String, String> consumer = (a, b, c, d, e) -> {
			throw exception;
		};

		RuntimeException actual = assertThrows(
				RuntimeException.class,
				() -> consumer.andThen((a, b, c, d, e) -> builder.append("after")).accept("v", "w", "x", "y", "z")
		);

		assertEquals(exception, actual);
		assertEquals("", builder.toString());
	}

	@Test
	void shouldRejectNullAfterConsumer() {
		QuintConsumer<String, String, String, String, String> consumer = (a, b, c, d, e) -> {
		};

		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
}
