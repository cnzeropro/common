package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.ref.ReferenceType;

import java.lang.ref.Reference;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
class ReferenceBoundedMapTest {
	private final List<ExecutorService> executors = new ArrayList<>();
	private final List<ReferenceBoundedMap<?, ?>> asyncMaps = new ArrayList<>();

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
		for (ReferenceBoundedMap<?, ?> map : asyncMaps) {
			map.destroy();
		}
		asyncMaps.clear();
		for (ExecutorService executor : executors) {
			executor.shutdownNow();
		}
		executors.clear();
	}

	@Test
	void putIfAbsentReplaceAndGetOrDefaultRespectPresenceInsteadOfNullValue() {
		List<PurgeEvent> expiredEvents = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

		map.put("existing", "value-1");
		assertEquals("value-1", map.putIfAbsent("existing", "value-2"));
		assertEquals("value-1", map.get("existing"));

		map.put("nullable", null);
		assertTrue(map.containsKey("nullable"));
		assertNull(map.getOrDefault("nullable", "fallback"));
		assertNull(map.putIfAbsent("nullable", "replacement"));
		assertNull(map.get("nullable"));
		assertTrue(map.entrySet().contains(new AbstractMap.SimpleEntry<>("nullable", null)));

		assertEquals("value-1", map.replace("existing", "value-3"));
		assertEquals("value-3", map.get("existing"));
		assertTrue(map.replace("existing", "value-3", "value-4"));
		assertEquals("value-4", map.get("existing"));
		assertEquals("value-4", map.replace("existing", "value-5", ReferenceType.STRONG, Duration.ofMillis(40), listener(expiredEvents)));
		assertEquals("value-5", map.get("existing"));

		sleep(Duration.ofMillis(80));
		assertNull(map.get("existing"));
		assertEvents(expiredEvents, event("existing", "value-5", PurgeReason.EXPIRED));
	}

	@Test
	void replaceAllUpdatesEntriesInPlace() {
		ReferenceBoundedMap<String, String> map = this.newLazyMap();
		map.put("a", "1");
		map.put("b", "2");

		map.replaceAll((key, value) -> key + value);

		assertEquals("a1", map.get("a"));
		assertEquals("b2", map.get("b"));
	}

	@Test
	void bulkOperationsAndViewsCoverCoreMapSurface() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();
		Map<String, String> bulk = new LinkedHashMap<>();
		bulk.put("a", "A");
		bulk.put("b", "B");

		map.putAll(bulk, listener(events));
		assertEquals(2, map.size());
		assertTrue(map.keySet().contains("a"));
		assertTrue(map.values().contains("B"));
		assertTrue(map.entrySet().contains(new AbstractMap.SimpleEntry<>("a", "A")));

		assertEquals("A", map.put("a", "A2", listener(events)));
		assertFalse(map.remove("b", "x"));
		assertTrue(map.entrySet().remove(new AbstractMap.SimpleEntry<>("b", "B")));

		map.put("nullable", null, listener(events));
		assertTrue(map.values().remove(null));
		assertFalse(map.containsKey("nullable"));
		assertEquals("{a=A2}", map.toString());
		assertEvents(events,
				event("a", "A", PurgeReason.REPLACED),
				event("b", "B", PurgeReason.EXPLICIT),
				event("nullable", null, PurgeReason.EXPLICIT)
		);
	}

	@Test
	void computeMethodsRespectPairPresenceForNullAndPhantomValues() {
		ReferenceBoundedMap<String, String> map = this.newLazyMap();
		AtomicInteger absentCalls = new AtomicInteger();
		AtomicInteger mergeCalls = new AtomicInteger();

		map.put("nullable", null);
		assertNull(map.computeIfAbsent("nullable", key -> {
			absentCalls.incrementAndGet();
			return "replacement";
		}));
		assertEquals(0, absentCalls.get());
		assertTrue(map.containsKey("nullable"));

		assertEquals("present-null", map.computeIfPresent("nullable", (key, value) -> {
			assertNull(value);
			return "present-null";
		}));
		assertEquals("present-null", map.get("nullable"));

		map.put("nullable", null);
		assertEquals("computed", map.compute("nullable", (key, value) -> {
			assertNull(value);
			return "computed";
		}));
		assertEquals("computed", map.get("nullable"));

		map.put("nullable", null);
		assertEquals("merged", map.merge("nullable", "incoming", (oldValue, incoming) -> {
			mergeCalls.incrementAndGet();
			assertNull(oldValue);
			assertEquals("incoming", incoming);
			return "merged";
		}));
		assertEquals(1, mergeCalls.get());
		assertEquals("merged", map.get("nullable"));

		ReferenceBoundedMap<String, String> phantomMap = ReferenceBoundedMap.<String, String>builder()
				.referenceType(ReferenceType.PHANTOM)
				.lazyCleanup(true)
				.build();
		assertNull(phantomMap.put("phantom", "value"));
		assertNull(phantomMap.computeIfAbsent("phantom", key -> {
			absentCalls.incrementAndGet();
			return "replacement";
		}));
		assertEquals(0, absentCalls.get());
		assertTrue(phantomMap.containsKey("phantom"));
	}

	@Test
	void computeMergeAndCapacityEvictionCoverDefaultMapSurface() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = ReferenceBoundedMap.<String, String>builder()
				.maxCapacity(2)
				.lazyCleanup(true)
				.listener(listener(events))
				.build();
		AtomicInteger absentCalls = new AtomicInteger();

		assertEquals("1", map.computeIfAbsent("a", key -> "1"));
		assertEquals("2", map.computeIfAbsent("b", key -> "2"));
		assertEquals("1", map.computeIfAbsent("a", key -> {
			absentCalls.incrementAndGet();
			return "replacement";
		}));
		assertEquals(0, absentCalls.get());

		assertEquals("1x", map.computeIfPresent("a", (key, value) -> value + "x"));
		assertNull(map.compute("b", (key, value) -> null));

		assertEquals("3", map.merge("c", "3", String::concat));
		assertEquals("4", map.merge("d", "4", String::concat));
		assertFalse(map.containsKey("a"));
		assertTrue(map.containsKey("c"));
		assertTrue(map.containsKey("d"));

		assertEquals("3x", map.merge("c", "x", String::concat));
		assertNull(map.merge("c", "remove", (oldValue, value) -> null));
		assertFalse(map.containsKey("c"));
		assertEquals("4", map.get("d"));
		assertEvents(events,
				event("b", "2", PurgeReason.EXPLICIT),
				event("a", "1x", PurgeReason.EVICTION),
				event("c", "3x", PurgeReason.EXPLICIT)
		);
	}

	@Test
	void maxCapacityEvictsUsingConfiguredOrder() {
		List<PurgeEvent> insertionEvents = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> insertionOrderMap = ReferenceBoundedMap.<String, String>builder()
				.maxCapacity(2)
				.storage(new LinkedHashMap<String, ReferenceBoundedMap.Pair<String, String>>())
				.lazyCleanup(true)
				.listener(listener(insertionEvents))
				.build();
		insertionOrderMap.put("a", "1");
		insertionOrderMap.put("b", "2");
		insertionOrderMap.get("a");
		insertionOrderMap.put("c", "3");
		assertFalse(insertionOrderMap.containsKey("a"));
		assertTrue(insertionOrderMap.containsKey("b"));
		assertTrue(insertionOrderMap.containsKey("c"));
		assertEvents(insertionEvents, event("a", "1", PurgeReason.EVICTION));

		List<PurgeEvent> accessEvents = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> accessOrderMap = ReferenceBoundedMap.<String, String>builder()
				.maxCapacity(2)
				.accessOrder(true)
				.storage(new LinkedHashMap<String, ReferenceBoundedMap.Pair<String, String>>(16, 0.75F, true))
				.lazyCleanup(true)
				.listener(listener(accessEvents))
				.build();
		accessOrderMap.put("a", "1");
		accessOrderMap.put("b", "2");
		accessOrderMap.get("a");
		accessOrderMap.put("c", "3");
		assertTrue(accessOrderMap.containsKey("a"));
		assertFalse(accessOrderMap.containsKey("b"));
		assertTrue(accessOrderMap.containsKey("c"));
		assertEvents(accessEvents, event("b", "2", PurgeReason.EVICTION));
	}

	@Test
	void staleExpiredAndCollectedCleanupDoNotRemoveNewMapping() {
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

		ReferenceBoundedMap.Pair<String, String> expiredOldPair = map.createPair("expiry", "old", ReferenceType.STRONG, Instant.now().minusMillis(1), null);
		ReferenceBoundedMap.Pair<String, String> expiryNewPair = map.createPair("expiry", "new", ReferenceType.STRONG, (Instant) null, null);
		map.writeLock.lock();
		try {
			map.storage.put("expiry", expiredOldPair);
			map.delayQueue.add(expiredOldPair);
			map.storage.put("expiry", expiryNewPair);
		} finally {
			map.writeLock.unlock();
		}
		map.cleanupExpiredEntries(false);
		assertEquals("new", map.get("expiry"));

		ReferenceBoundedMap.Pair<String, String> reclaimedOldPair = map.createPair("reclaim", "old", ReferenceType.WEAK, (Instant) null, null);
		ReferenceBoundedMap.Pair<String, String> reclaimNewPair = map.createPair("reclaim", "new", ReferenceType.STRONG, (Instant) null, null);
		map.writeLock.lock();
		try {
			map.storage.put("reclaim", reclaimedOldPair);
			map.storage.put("reclaim", reclaimNewPair);
		} finally {
			map.writeLock.unlock();
		}
		assertTrue(((Reference<?>) reclaimedOldPair).enqueue());
		map.cleanupCollectedEntries(false);
		assertEquals("new", map.get("reclaim"));
	}

	@Test
	void referenceQueueCleanupNotifiesCollectedAndRemovesPair() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();
		ReferenceBoundedMap.Pair<String, String> pair = map.createPair(
				"weak",
				"value",
				ReferenceType.WEAK,
				(Instant) null,
				listener(events)
		);

		map.writeLock.lock();
		try {
			map.storage.put("weak", pair);
		} finally {
			map.writeLock.unlock();
		}

		assertTrue(((Reference<?>) pair).enqueue());
		map.cleanupCollectedEntries(false);
		assertFalse(map.containsKey("weak"));
		assertEvents(events, event("weak", null, PurgeReason.COLLECTED));
	}

	@Test
	void clearAndViewsAreBacked() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

		map.put("first", "1", listener(events));
		map.put("second", "2", listener(events));
		assertTrue(map.keySet().remove("first"));
		assertFalse(map.containsKey("first"));

		java.util.Map.Entry<String, String> entry = map.entrySet().iterator().next();
		assertEquals("2", entry.setValue("updated"));
		assertEquals("updated", map.get(entry.getKey()));
		assertTrue(map.values().remove("updated"));
		assertTrue(map.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> map.entrySet().add(new AbstractMap.SimpleEntry<>("third", "3")));

		map.put("clear-1", "A", listener(events));
		map.put("clear-2", "B", listener(events));
		map.clear();
		assertTrue(map.isEmpty());
		assertEvents(events,
				event("first", "1", PurgeReason.EXPLICIT),
				event("second", "updated", PurgeReason.EXPLICIT),
				event("clear-1", "A", PurgeReason.CLEARED),
				event("clear-2", "B", PurgeReason.CLEARED)
		);
	}

	@Test
	void clearNotifiesExpiredEntriesAsExpired() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

		map.put("expired", "value", Duration.ofMillis(40), listener(events));
		sleep(Duration.ofMillis(80));
		map.clear();

		assertTrue(map.isEmpty());
		assertEvents(events, event("expired", "value", PurgeReason.EXPIRED));
	}

	@Test
	void clearReportsExpiredAndClearedForMixedEntries() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

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
	void removeByKeyValueAndIteratorRemoveRespectBackedViewContract() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> map = this.newLazyMap();

		map.put("nullable", null, listener(events));
		assertTrue(map.remove("nullable", null));
		assertFalse(map.containsKey("nullable"));

		map.put("first", "1", listener(events));
		map.put("second", "2", listener(events));
		Iterator<String> iterator = map.keySet().iterator();
		String removedKey = iterator.next();
		String removedValue = map.get(removedKey);
		iterator.remove();

		assertFalse(map.containsKey(removedKey));
		assertThrows(IllegalStateException.class, iterator::remove);
		assertTrue(iterator.hasNext());
		assertFalse(iterator.next().equals(removedKey));
		assertEvents(events,
				event("nullable", null, PurgeReason.EXPLICIT),
				event(removedKey, removedValue, PurgeReason.EXPLICIT)
		);
	}

	@Test
	void removeAndViewsTreatExpiredEntriesAsAbsent() {
		List<PurgeEvent> events = new CopyOnWriteArrayList<>();
		ReferenceBoundedMap<String, String> removeMap = this.newLazyMap();

		removeMap.put("expired", "value", Duration.ofMillis(40), listener(events));
		sleep(Duration.ofMillis(80));
		assertNull(removeMap.remove("expired"));
		assertFalse(removeMap.containsKey("expired"));
		assertEvents(events, event("expired", "value", PurgeReason.EXPIRED));

		ReferenceBoundedMap<String, String> iteratorMap = this.newLazyMap();
		iteratorMap.put("key", "value", Duration.ofMillis(40), listener(events));
		Iterator<String> valueIterator = iteratorMap.values().iterator();
		sleep(Duration.ofMillis(80));
		assertFalse(valueIterator.hasNext());
		assertNull(iteratorMap.get("key"));

		ReferenceBoundedMap<String, String> entryMap = this.newLazyMap();
		entryMap.put("entry", "value", Duration.ofMillis(40));
		java.util.Map.Entry<String, String> entry = entryMap.entrySet().iterator().next();
		sleep(Duration.ofMillis(80));
		assertThrows(IllegalStateException.class, () -> entry.setValue("new-value"));
		assertNull(entryMap.get("entry"));
	}

	@Test
	void phantomEntriesRemainPresentEvenThoughValueIsNull() {
		ReferenceBoundedMap<String, String> map = ReferenceBoundedMap.<String, String>builder()
				.referenceType(ReferenceType.PHANTOM)
				.lazyCleanup(true)
				.build();

		assertNull(map.put("phantom", "value"));
		assertTrue(map.containsKey("phantom"));
		assertNull(map.get("phantom"));
		assertNull(map.getOrDefault("phantom", "fallback"));
		assertTrue(map.entrySet().contains(new AbstractMap.SimpleEntry<>("phantom", null)));
		assertNull(map.putIfAbsent("phantom", "replacement"));
		assertEquals(1, map.size());
	}

	@Test
	void sharedDefaultCleanupExecutorSurvivesSingleMapDestroy() {
		AtomicInteger purgeCount = new AtomicInteger();
		ReferenceBoundedMap<String, String> map1 = ReferenceBoundedMap.<String, String>builder()
				.lazyCleanup(false)
				.build();
		ReferenceBoundedMap<String, String> map2 = ReferenceBoundedMap.<String, String>builder()
				.lazyCleanup(false)
				.listener((key, value, reason) -> purgeCount.incrementAndGet())
				.build();
		asyncMaps.add(map1);
		asyncMaps.add(map2);

		try {
			map1.destroy();
		} catch (Exception e) {
			throw new AssertionError(e);
		}

		map2.put("key", "value", Duration.ofMillis(40));
		await(() -> purgeCount.get() >= 1, Duration.ofSeconds(2));
		assertNull(map2.get("key"));
	}

	@Test
	void sharedDefaultCleanupExecutorSupportsMultipleLiveMaps() {
		AtomicInteger firstMapPurges = new AtomicInteger();
		AtomicInteger secondMapPurges = new AtomicInteger();
		ReferenceBoundedMap<String, String> map1 = ReferenceBoundedMap.<String, String>builder()
				.lazyCleanup(false)
				.listener((key, value, reason) -> firstMapPurges.incrementAndGet())
				.build();
		ReferenceBoundedMap<String, String> map2 = ReferenceBoundedMap.<String, String>builder()
				.lazyCleanup(false)
				.listener((key, value, reason) -> secondMapPurges.incrementAndGet())
				.build();
		asyncMaps.add(map1);
		asyncMaps.add(map2);

		map1.put("first", "A", Duration.ofMillis(40));
		map2.put("second", "B", Duration.ofMillis(40));

		await(() -> firstMapPurges.get() >= 1 && secondMapPurges.get() >= 1, Duration.ofSeconds(2));
		assertNull(map1.get("first"));
		assertNull(map2.get("second"));
	}

	@Test
	void snapshotIteratorToleratesConcurrentMutation() {
		ReferenceBoundedMap<String, String> map = this.newLazyMap();
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
	}

	@Test
	void destroyDoesNotShutdownSharedExecutorOrBreakOtherMapCleanup() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		executors.add(executor);
		AtomicInteger purgeCount = new AtomicInteger();
		ReferenceBoundedMap<String, String> map1 = ReferenceBoundedMap.<String, String>builder()
				.cleanupExecutor(executor)
				.lazyCleanup(false)
				.build();
		ReferenceBoundedMap<String, String> map2 = ReferenceBoundedMap.<String, String>builder()
				.cleanupExecutor(executor)
				.lazyCleanup(false)
				.listener((key, value, reason) -> purgeCount.incrementAndGet())
				.build();
		asyncMaps.add(map1);
		asyncMaps.add(map2);

		map1.destroy();
		Future<Integer> future = executor.submit(() -> 1);
		assertEquals(Integer.valueOf(1), future.get());

		map2.put("key", "value", Duration.ofMillis(40));
		await(() -> purgeCount.get() >= 1, Duration.ofSeconds(2));
		assertNull(map2.get("key"));
	}

	@Test
	void asyncCleanupSurvivesListenerException() {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executors.add(executor);
		AtomicInteger purgeCount = new AtomicInteger();
		ReferenceBoundedMap<String, String> map = ReferenceBoundedMap.<String, String>builder()
				.cleanupExecutor(executor)
				.lazyCleanup(false)
				.listener((key, value, reason) -> {
					purgeCount.incrementAndGet();
					throw new IllegalStateException("boom");
				})
				.build();
		asyncMaps.add(map);

		map.put("first", "A", Duration.ofMillis(40));
		map.put("second", "B", Duration.ofMillis(120));

		await(() -> purgeCount.get() >= 2, Duration.ofSeconds(2));
		assertNull(map.get("first"));
		assertNull(map.get("second"));
	}

	private ReferenceBoundedMap<String, String> newLazyMap() {
		return ReferenceBoundedMap.<String, String>builder()
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
