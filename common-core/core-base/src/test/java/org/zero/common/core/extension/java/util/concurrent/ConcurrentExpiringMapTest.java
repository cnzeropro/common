package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.PurgeListener;
import org.zero.common.core.extension.java.util.PurgeReason;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/2
 */
class ConcurrentExpiringMapTest {
	private final List<ConcurrentExpiringMap<?, ?>> asyncMaps = new ArrayList<>();

	private static PurgeListener<String, String> listener(List<PurgeEvent> events) {
		return (key, value, reason) -> events.add(new PurgeEvent(key, value, reason));
	}

	private static PurgeEvent event(String key, String value, PurgeReason reason) {
		return new PurgeEvent(key, value, reason);
	}

	private static void assertEvents(List<PurgeEvent> actual, PurgeEvent... expected) {
		assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual.get(i));
		}
	}

	private static void await(BooleanSupplier condition, Duration timeout) {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (System.nanoTime() < deadline) {
			if (condition.getAsBoolean()) {
				return;
			}
			sleep(Duration.ofMillis(10));
		}
		assertTrue(condition.getAsBoolean(), "Condition was not satisfied in time.");
	}

	private static void sleep(Duration duration) {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError(e);
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		for (ConcurrentExpiringMap<?, ?> map : asyncMaps) {
			map.destroy();
		}
		asyncMaps.clear();
	}

	@Test
	void computeRebuildsExpiredEntryWithNewMetadata() {
		List<PurgeEvent> oldEvents = new CopyOnWriteArrayList<>();
		List<PurgeEvent> newEvents = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		map.put("key", "old", Duration.ofMillis(40), listener(oldEvents));
		sleep(Duration.ofMillis(80));

		assertEquals("new", map.compute("key", (key, value) -> {
			assertNull(value);
			return "new";
		}, Duration.ofMillis(40), listener(newEvents)));
		assertEquals("new", map.get("key"));
		assertEvents(oldEvents, event("key", "old", PurgeReason.EXPIRED));

		sleep(Duration.ofMillis(80));
		assertNull(map.get("key"));
		assertEvents(newEvents, event("key", "new", PurgeReason.EXPIRED));
	}

	@Test
	void mergeKeepsOriginalMetadataForLiveEntry() {
		List<PurgeEvent> originalEvents = new CopyOnWriteArrayList<>();
		List<PurgeEvent> replacementEvents = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		map.put("key", "A", Duration.ofMillis(80), listener(originalEvents));
		assertEquals("AB", map.merge("key", "B", String::concat, Duration.ofSeconds(1), listener(replacementEvents)));
		assertEquals("AB", map.get("key"));

		sleep(Duration.ofMillis(120));
		assertNull(map.get("key"));
		assertEvents(originalEvents, event("key", "AB", PurgeReason.EXPIRED));
		assertTrue(replacementEvents.isEmpty());
	}

	@Test
	void pairValueIsVolatileAndSetValueReturnsPreviousValue() throws NoSuchFieldException {
		ConcurrentExpiringMap.Pair<String, String> pair = new ConcurrentExpiringMap.Pair<>("key", "old", null, null);

		assertTrue(Modifier.isVolatile(ConcurrentExpiringMap.Pair.class.getDeclaredField("value").getModifiers()));
		assertEquals("old", pair.setValue("new"));
		assertEquals("new", pair.getValue());
	}

	@Test
	void iteratorAndEntryViewFollowMapContract() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		map.put("first", "1");
		map.put("second", "2");

		Iterator<String> keyIterator = map.keySet().iterator();
		String firstKey = keyIterator.next();
		assertTrue(keyIterator.hasNext());
		keyIterator.remove();
		assertFalse(map.containsKey(firstKey));
		assertThrows(IllegalStateException.class, keyIterator::remove);

		String secondKey = keyIterator.next();
		assertNotEquals(firstKey, secondKey);
		assertEquals(1, map.size());
		assertFalse(keyIterator.hasNext());
		assertThrows(NoSuchElementException.class, keyIterator::next);

		Map.Entry<String, String> entry = map.entrySet().iterator().next();
		assertEquals("2", entry.setValue("updated"));
		assertEquals("updated", map.get(entry.getKey()));
		assertThrows(UnsupportedOperationException.class, () -> map.entrySet().add(new AbstractMap.SimpleEntry<>("third", "3")));
	}

	@Test
	void coreMapOperationsAndViewsStayConsistent() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		assertTrue(map.isEmpty());
		assertNull(map.put("a", "1", (Duration) null, listener(events)));
		assertEquals("1", map.put("a", "2", (Duration) null, listener(events)));

		Map<String, String> bulk = new LinkedHashMap<>();
		bulk.put("b", "2");
		bulk.put("c", "3");
		map.putAll(bulk, (Duration) null, listener(events));

		assertEquals(3, map.size());
		assertTrue(map.keySet().contains("b"));
		assertTrue(map.values().contains("3"));
		assertTrue(map.entrySet().contains(new AbstractMap.SimpleEntry<>("c", "3")));

		assertFalse(map.remove("b", "x"));
		assertTrue(map.entrySet().remove(new AbstractMap.SimpleEntry<>("b", "2")));
		assertTrue(map.values().remove("3"));

		assertFalse(map.containsKey("b"));
		assertFalse(map.containsValue("3"));
		assertEquals("{a=2}", map.toString());
		assertEvents(events,
				event("a", "1", PurgeReason.REPLACED),
				event("b", "2", PurgeReason.EXPLICIT),
				event("c", "3", PurgeReason.EXPLICIT)
		);
	}

	@Test
	void computeMergeAndReplaceAllCoverDefaultConcurrentMapSurface() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		AtomicInteger absentCalls = new AtomicInteger();

		assertEquals("1", map.computeIfAbsent("a", key -> "1"));
		assertEquals("1", map.computeIfAbsent("a", key -> {
			absentCalls.incrementAndGet();
			return "replacement";
		}));
		assertEquals(0, absentCalls.get());

		assertEquals("12", map.computeIfPresent("a", (key, value) -> value + "2"));
		assertNull(map.computeIfPresent("missing", (key, value) -> "x"));
		map.replaceAll((key, value) -> value + "R");
		assertEquals("12R", map.get("a"));

		assertNull(map.compute("a", (key, value) -> null));
		assertFalse(map.containsKey("a"));

		assertEquals("A", map.merge("m", "A", String::concat));
		assertEquals("AB", map.merge("m", "B", String::concat));
		assertNull(map.merge("m", "C", (oldValue, value) -> null));
		assertFalse(map.containsKey("m"));
	}

	@Test
	void explicitRemovalAndClearNotifyListenerWithoutTimeToLive() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		map.put("remove", "A", (Duration) null, listener(events));
		assertEquals("A", map.remove("remove"));
		map.put("clear-1", "B", (Duration) null, listener(events));
		map.put("clear-2", "C", (Duration) null, listener(events));
		map.clear();

		assertEvents(events,
				event("remove", "A", PurgeReason.EXPLICIT),
				event("clear-1", "B", PurgeReason.CLEARED),
				event("clear-2", "C", PurgeReason.CLEARED)
		);
	}

	@Test
	void clearReportsExpiredAndClearedForMixedEntries() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		map.put("live", "A", (Duration) null, listener(events));
		map.put("expired", "B", Duration.ofMillis(40), listener(events));
		sleep(Duration.ofMillis(80));
		map.clear();

		assertTrue(map.isEmpty());
		assertEvents(events,
				event("live", "A", PurgeReason.CLEARED),
				event("expired", "B", PurgeReason.EXPIRED)
		);
	}

	@Test
	void expiredEntriesAreInvisibleToContainsValueAndForEach() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		map.put("expired", "value", Duration.ofMillis(40));
		sleep(Duration.ofMillis(80));

		assertFalse(map.containsValue("value"));
		AtomicInteger visited = new AtomicInteger();
		map.forEach((key, value) -> visited.incrementAndGet());
		assertEquals(0, visited.get());
	}

	@Test
	void liveComputeAndMergeRemovalUseExplicitReason() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();

		map.put("compute", "A", Duration.ofSeconds(1), listener(events));
		map.put("merge", "B", Duration.ofSeconds(1), listener(events));

		assertNull(map.compute("compute", (key, value) -> {
			assertEquals("A", value);
			return null;
		}));
		assertNull(map.merge("merge", "X", (left, right) -> null));
		assertFalse(map.containsKey("compute"));
		assertFalse(map.containsKey("merge"));
		assertEvents(events,
				event("compute", "A", PurgeReason.EXPLICIT),
				event("merge", "B", PurgeReason.EXPLICIT)
		);
	}

	@Test
	void concurrentMapContractRejectsNullMutations() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		map.put("entry", "value");

		assertThrows(NullPointerException.class, () -> map.put(null, "value"));
		assertThrows(NullPointerException.class, () -> map.put("key", null));
		assertThrows(NullPointerException.class, () -> map.putIfAbsent("key", null));
		assertThrows(NullPointerException.class, () -> map.remove("entry", null));
		assertThrows(NullPointerException.class, () -> map.compute(null, (key, value) -> "x"));
		assertThrows(NullPointerException.class, () -> map.merge("entry", null, String::concat));

		Map.Entry<String, String> entry = map.entrySet().iterator().next();
		assertThrows(NullPointerException.class, () -> entry.setValue(null));
	}

	@Test
	void expiredRemovalEqualsHashCodeAndToStringRespectLiveView() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		ConcurrentExpiringMap<String, String> expected = this.newLazyMap();

		map.put("live", "1");
		expected.put("live", "1");
		map.put("expired", "gone", Duration.ofMillis(40), listener(events));

		sleep(Duration.ofMillis(80));
		assertEquals("fallback", map.getOrDefault("expired", "fallback"));
		assertNull(map.remove("expired"));
		assertEquals(expected, map);
		assertEquals(expected.hashCode(), map.hashCode());
		assertEquals("{live=1}", map.toString());
		assertEvents(events, event("expired", "gone", PurgeReason.EXPIRED));
	}

	@Test
	void snapshotIteratorToleratesConcurrentMutation() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		map.put("first", "1");
		map.put("second", "2");

		Iterator<String> iterator = map.keySet().iterator();
		map.put("third", "3");
		map.remove("first");

		List<String> iterated = new ArrayList<>();
		while (iterator.hasNext()) {
			iterated.add(iterator.next());
		}

		assertEquals(1, iterated.size());
		assertEquals("second", iterated.get(0));
		assertFalse(iterated.contains("third"));
	}

	@Test
	void staleDelayedPairDoesNotRemoveNewMapping() {
		ConcurrentExpiringMap<String, String> map = this.newLazyMap();
		ConcurrentExpiringMap.Pair<String, String> oldPair = map.createPair("key", "old", Duration.ofMillis(-1), null);
		ConcurrentExpiringMap.Pair<String, String> newPair = map.createPair("key", "new", (Duration) null, null);

		map.writeLock.lock();
		try {
			map.storage.put("key", oldPair);
			map.delayQueue.add(oldPair);
			map.storage.put("key", newPair);
		} finally {
			map.writeLock.unlock();
		}

		map.cleanupExpiredEntries(false);
		assertEquals("new", map.get("key"));
	}

	@Test
	void asyncCleanupSurvivesListenerException() {
		AtomicInteger purgeCount = new AtomicInteger();
		ConcurrentExpiringMap<String, String> map = ConcurrentExpiringMap.<String, String>builder()
				.storage(new LinkedHashMap<>())
				.listener((key, value, reason) -> {
					purgeCount.incrementAndGet();
					throw new IllegalStateException("boom");
				})
				.lazyCleanup(false)
				.build();
		asyncMaps.add(map);

		map.put("first", "A", Duration.ofMillis(40));
		map.put("second", "B", Duration.ofMillis(120));

		await(() -> purgeCount.get() >= 2, Duration.ofSeconds(2));
		assertNull(map.get("first"));
		assertNull(map.get("second"));
	}

	private ConcurrentExpiringMap<String, String> newLazyMap() {
		return ConcurrentExpiringMap.<String, String>builder()
				.storage(new LinkedHashMap<>())
				.lazyCleanup(true)
				.build();
	}

	private static final class PurgeEvent {
		private final String key;
		private final String value;
		private final PurgeReason reason;

		private PurgeEvent(String key, String value, PurgeReason reason) {
			this.key = key;
			this.value = value;
			this.reason = reason;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof PurgeEvent)) {
				return false;
			}
			PurgeEvent other = (PurgeEvent) obj;
			return java.util.Objects.equals(key, other.key)
					&& java.util.Objects.equals(value, other.value)
					&& reason == other.reason;
		}

		@Override
		public int hashCode() {
			return java.util.Objects.hash(key, value, reason);
		}

		@Override
		public String toString() {
			return "PurgeEvent{"
					+ "key='" + key + '\''
					+ ", value='" + value + '\''
					+ ", reason=" + reason
					+ '}';
		}
	}
}
