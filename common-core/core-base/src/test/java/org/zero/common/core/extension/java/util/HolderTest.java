package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class HolderTest {

	@Test
	void emptyCreatesIndependentMutableInstances() {
		Holder<String> first = Holder.empty();
		Holder<String> second = Holder.empty();

		first.set("value");

		assertNotSame(first, second);
		assertEquals("value", first.get());
		assertTrue(second.isEmpty());
		assertTrue(Holder.of(null).isEmpty());
		assertTrue(Holder.from((Optional<String>) null).isEmpty());
		assertTrue(Holder.from((Option<String>) null).isEmpty());
		assertTrue(Holder.from(() -> null).isEmpty());
	}

	@Test
	void setNullClearsCurrentHolder() {
		Holder<String> holder = Holder.of("value");

		assertSame(holder, holder.set(null));

		assertTrue(holder.isEmpty());
		assertNull(holder.get());
	}

	@Test
	void mutationHelpersReplaceAndClearValuesInPlace() {
		Holder<String> holder = Holder.empty();

		assertSame(holder, holder.set("a"));
		assertEquals("a", holder.getAndSet("b"));
		assertEquals("b", holder.get());
		assertEquals("b", holder.getAndClear());
		assertTrue(holder.isEmpty());

		holder.update(value -> value == null ? "created" : value + "-updated");
		assertEquals("created", holder.get());
		holder.updateIfPresent(value -> value + "-updated");
		assertEquals("created-updated", holder.get());
		holder.clear();
		holder.updateIfPresent(value -> "ignored");
		assertTrue(holder.isEmpty());
	}

	@Test
	void setIfEmptyOnlyFillEmptyHolder() {
		AtomicInteger fallbackCalls = new AtomicInteger();
		Holder<String> holder = Holder.of("primary");

		holder.setIfEmpty(() -> {
			fallbackCalls.incrementAndGet();
			return "fallback";
		});
		Holder<String> emptyHolder = Holder.<String>empty().setIfEmpty(() -> {
			fallbackCalls.incrementAndGet();
			return "fallback";
		});

		assertEquals("primary", holder.get());
		assertEquals("fallback", emptyHolder.get());
		assertEquals(1, fallbackCalls.get());
	}

	@Test
	void chainHelpersExposeCurrentValueAsOption() {
		List<String> events = new ArrayList<>();
		Holder<String> holder = Holder.of("zero");

		Integer value = holder.peekAll(events::add, item -> events.add(item.toUpperCase()))
				.retainIf(item -> item.startsWith("z"))
				.map(String::length)
				.get();

		assertEquals(Integer.valueOf(4), value);
		assertIterableEquals(Arrays.asList("zero", "ZERO"), events);
		assertEquals("zero!", holder.flatMap(item -> Option.of(item + "!")).get());
		assertEquals(Integer.valueOf(4), holder.flatMapOptional(item -> Optional.of(item.length())).get());
	}

	@Test
	void conversionAndQueryHelpersReadCurrentState() {
		List<Integer> lengths = Holder.of("ab")
				.flatMapStream(value -> Stream.of(value.length(), value.length() + 1))
				.collect(Collectors.toList());

		assertIterableEquals(Arrays.asList(2, 3), lengths);
		assertEquals(Optional.of("ab"), Holder.of("ab").optional());
		assertEquals("ab", Holder.of("ab").option().get());
		assertTrue(Holder.of("ab").contains("ab"));
		assertTrue(Holder.of("ab").isPresentAnd(value -> value.length() == 2));
		assertTrue(Holder.<String>empty().isEmptyOr(value -> value.length() == 2));
		assertFalse(Holder.of("ab").copy() == Holder.of("ab"));
	}

	@Test
	void emptyOrElseThrowReportsMissingValue() {
		assertThrows(NoSuchElementException.class, () -> Holder.empty().orElseThrow());
		assertThrows(IllegalStateException.class,
				() -> Holder.empty().orElseThrow(() -> new IllegalStateException("Missing value")));
		assertThrows(IllegalArgumentException.class,
				() -> Holder.empty().orElseThrow(IllegalArgumentException::new, "Missing value"));
	}
}
