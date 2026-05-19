package org.zero.common.core.util.alibaba.ttl;

import com.alibaba.ttl.TtlRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class TransmittableHolderTest {
	@AfterEach
	void tearDown() {
		TransmittableHolder.clear();
	}

	@Test
	void shouldSetGetRemoveAndClearValues() {
		TransmittableHolder.setVal("tenant", "zero");

		assertEquals("zero", TransmittableHolder.getVal("tenant", String.class));

		TransmittableHolder.remove("tenant");

		assertNull(TransmittableHolder.getVal("tenant"));

		TransmittableHolder.setVal("tenant", "zero");
		TransmittableHolder.clear();

		assertNull(TransmittableHolder.getVal("tenant"));
	}

	@Test
	void shouldTransmitContextToWrappedThreadPoolTask() throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		AtomicReference<String> captured = new AtomicReference<>();
		try {
			TransmittableHolder.setVal("tenant", "zero");

			Future<?> future =
					executorService.submit(TtlRunnable.get(() -> captured.set(TransmittableHolder.getVal("tenant", String.class))));
			future.get();

			assertEquals("zero", captured.get());
		} finally {
			executorService.shutdownNow();
		}
	}
}
