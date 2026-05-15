package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/14
 */
class SynchronizedLRUMapTest {

	private static boolean containsEntryKey(Set<Map.Entry<String, String>> entries, String key) {
		for (Map.Entry<String, String> entry : entries) {
			if (key.equals(entry.getKey())) {
				return true;
			}
		}
		return false;
	}

	@Test
	void shouldEvictLeastRecentlyUsedEntry() {
		Map<String, String> map = new SynchronizedLRUMap<>(3);
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");

		map.get("1");
		map.put("4", "4");

		assertEquals(3, map.size());
		assertTrue(map.containsKey("1"));
		assertFalse(map.containsKey("2"));
		assertEquals("3", map.get("3"));
		assertEquals("4", map.get("4"));
	}

	@Test
	void shouldRejectNonPositiveMaxCapacity() {
		assertThrows(IllegalArgumentException.class, () -> new SynchronizedLRUMap<>(0));
		assertThrows(IllegalArgumentException.class, () -> new SynchronizedLRUMap<>(-1));
	}

	@Test
	void shouldExposeMaxCapacity() {
		SynchronizedLRUMap<String, String> map = new SynchronizedLRUMap<>(2);

		assertEquals(2, map.maxCapacity());
	}

	@Test
	void shouldReturnImmutableSnapshotViews() {
		SynchronizedLRUMap<String, String> map = new SynchronizedLRUMap<>(3);
		map.put("1", "1");
		map.put("2", "2");

		Set<Map.Entry<String, String>> entries = map.entrySet();
		Set<String> keys = map.keySet();
		Collection<String> values = map.values();
		map.put("3", "3");

		assertEquals(2, entries.size());
		assertEquals(2, keys.size());
		assertEquals(2, values.size());
		assertFalse(containsEntryKey(entries, "3"));
		assertFalse(keys.contains("3"));
		assertFalse(values.contains("3"));
		assertThrows(UnsupportedOperationException.class, entries::clear);
		assertThrows(UnsupportedOperationException.class, keys::clear);
		assertThrows(UnsupportedOperationException.class, values::clear);
	}

	@Test
	void shouldAllowMutationWhileIteratingSnapshot() {
		SynchronizedLRUMap<String, String> map = new SynchronizedLRUMap<>(4);
		map.put("1", "1");
		map.put("2", "2");

		for (Map.Entry<String, String> entry : map.entrySet()) {
			map.put(entry.getKey() + "-copy", entry.getValue());
		}

		assertEquals(4, map.size());
		assertTrue(map.containsKey("1-copy"));
		assertTrue(map.containsKey("2-copy"));
	}

	@Test
	void shouldKeepCapacityUnderConcurrentAccess() throws Exception {
		int maxCapacity = 64;
		SynchronizedLRUMap<Integer, Integer> map = new SynchronizedLRUMap<>(maxCapacity);
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		CountDownLatch startLatch = new CountDownLatch(1);
		List<Future<?>> futures = new ArrayList<>();
		try {
			for (int threadIndex = 0; threadIndex < 8; threadIndex++) {
				final int offset = threadIndex * 10000;
				futures.add(executorService.submit(() -> {
					startLatch.await();
					for (int index = 0; index < 1000; index++) {
						int key = offset + index;
						map.put(key, key);
						map.get(key / 2);
						if (index % 3 == 0) {
							map.remove(key - 1);
						}
					}
					return null;
				}));
			}

			startLatch.countDown();
			for (Future<?> future : futures) {
				future.get(10, TimeUnit.SECONDS);
			}

			assertTrue(map.size() <= maxCapacity);
		} finally {
			executorService.shutdownNow();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldWorkAfterSerialization() throws Exception {
		SynchronizedLRUMap<String, String> map = new SynchronizedLRUMap<>(2);
		map.put("1", "1");
		map.put("2", "2");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
			objectOutputStream.writeObject(map);
		}

		SynchronizedLRUMap<String, String> deserializedMap;
		try (ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
			deserializedMap = (SynchronizedLRUMap<String, String>) objectInputStream.readObject();
		}

		deserializedMap.get("1");
		deserializedMap.put("3", "3");

		assertEquals(2, deserializedMap.size());
		assertTrue(deserializedMap.containsKey("1"));
		assertFalse(deserializedMap.containsKey("2"));
		assertEquals("3", deserializedMap.get("3"));
	}
}
