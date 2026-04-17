package org.zero.common.core.extension.java.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class ThreadBuilderTest {

	@Test
	void shouldNotConsumeGeneratedNameWhenExplicitTaskNameIsProvided() {
		long originalThreadNumber = ThreadBuilder.THREAD_NUMBER.get();
		try {
			ThreadBuilder.THREAD_NUMBER.set(1L);

			Thread namedThread = new ThreadBuilder().taskName("named-thread").build();
			Thread generatedThread = new ThreadBuilder().build();

			assertEquals("named-thread", namedThread.getName());
			assertEquals("Thread1", generatedThread.getName());
			assertEquals(2L, ThreadBuilder.THREAD_NUMBER.get());
		} finally {
			ThreadBuilder.THREAD_NUMBER.set(originalThreadNumber);
		}
	}
}
