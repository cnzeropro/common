package org.zero.common.core.extension.slf4j.spi;

import com.alibaba.ttl.TtlRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class CustomMDCAdapterTest {
	private final CustomMDCAdapter adapter = new CustomMDCAdapter();

	@AfterEach
	void tearDown() {
		adapter.clear();
	}

	@Test
	void shouldManageContextMap() {
		adapter.put("traceId", "root");

		Map<String, String> copy = adapter.getCopyOfContextMap();
		copy.put("traceId", "changed");

		assertEquals("root", adapter.get("traceId"));

		adapter.setContextMap(Collections.singletonMap("tenant", "zero"));
		assertNull(adapter.get("traceId"));
		assertEquals("zero", adapter.get("tenant"));

		adapter.remove("tenant");

		assertFalse(adapter.getCopyOfContextMap().containsKey("tenant"));
	}

	@Test
	void shouldManageDequeByKey() {
		adapter.pushByKey("traceId", "root");
		adapter.pushByKey("traceId", "child");

		Deque<String> copy = adapter.getCopyOfDequeByKey("traceId");

		assertEquals("child", copy.pop());
		assertEquals("child", adapter.popByKey("traceId"));
		assertEquals("root", adapter.popByKey("traceId"));
		assertNull(adapter.popByKey("traceId"));
	}

	@Test
	void shouldTransmitContextToWrappedThreadPoolTask() throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		AtomicReference<String> captured = new AtomicReference<>();
		try {
			adapter.put("traceId", "root");

			Future<?> future = executorService.submit(TtlRunnable.get(() -> captured.set(adapter.get("traceId"))));
			future.get();

			assertEquals("root", captured.get());
		} finally {
			executorService.shutdownNow();
		}
	}
}
