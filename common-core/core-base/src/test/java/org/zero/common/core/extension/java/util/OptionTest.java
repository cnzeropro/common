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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class OptionTest {

	@Test
	void emptyUsesSharedImmutableInstance() {
		Option<String> first = Option.empty();
		Option<Integer> second = Option.empty();

		assertSame(first, Option.<String>empty());
		assertSame(second, Option.<Integer>empty());
		assertTrue(first.isEmpty());
		assertFalse(first.isPresent());
		assertNull(first.getOrNull());
		assertEquals("fallback", first.orElse("fallback"));
	}

	@Test
	void factoriesTreatNullAsEmpty() {
		assertEquals("value", Option.of("value").get());
		assertEquals("value", Option.from(Optional.of("value")).get());
		assertEquals("value", Option.from(Holder.of("value")).get());
		assertTrue(Option.of(null).isEmpty());
		assertTrue(Option.from((Optional<String>) null).isEmpty());
		assertTrue(Option.from((Holder<String>) null).isEmpty());
		assertTrue(Option.from(Holder.empty()).isEmpty());
		assertTrue(Option.from(Optional.empty()).isEmpty());
		assertTrue(Option.from(() -> null).isEmpty());
	}

	@Test
	void fromHolderCreatesImmutableSnapshot() {
		Holder<String> holder = Holder.of("before");

		Option<String> option = Option.from(holder);
		holder.set("after");

		assertEquals("before", option.get());
		assertEquals("after", holder.get());
	}

	@Test
	void chainOperationsKeepOptionalStyleSemantics() {
		AtomicInteger fallbackCalls = new AtomicInteger();
		List<String> events = new ArrayList<>();

		String value = Option.of("zero")
				.peekAll(events::add, item -> events.add(item.toUpperCase()))
				.filter(item -> item.startsWith("z"))
				.map(item -> item + "-1")
				.flatMap(item -> Option.of(item.length()))
				.flatMapOptional(length -> Optional.of("len-" + length))
				.or(() -> {
					fallbackCalls.incrementAndGet();
					return Option.of("fallback");
				})
				.get();

		assertEquals("len-6", value);
		assertEquals(0, fallbackCalls.get());
		assertIterableEquals(Arrays.asList("zero", "ZERO"), events);
	}

	@Test
	void emptyOptionUsesLazyFallbacks() {
		AtomicInteger fallbackCalls = new AtomicInteger();

		assertEquals("fallback", Option.<String>empty().or(() -> {
			fallbackCalls.incrementAndGet();
			return Option.of("fallback");
		}).get());
		assertEquals("lazy", Option.<String>empty().orElseGet(() -> "lazy"));
		assertEquals("value", Option.<String>empty().orElse("value"));
		assertEquals(1, fallbackCalls.get());
	}

	@Test
	void conversionAndQueryHelpersExposeValueSafely() {
		List<Integer> lengths = Option.of("ab")
				.flatMapStream(value -> Stream.of(value.length(), value.length() + 1))
				.collect(Collectors.toList());

		assertIterableEquals(Arrays.asList(2, 3), lengths);
		assertEquals(Optional.of("ab"), Option.of("ab").optional());
		assertTrue(Option.of("ab").contains("ab"));
		assertTrue(Option.of("ab").isPresentAnd(value -> value.length() == 2));
		assertTrue(Option.<String>empty().isEmptyOr(value -> value.length() == 2));
		assertEquals("ab", Option.<Object>of("ab").cast(String.class).get());
		assertTrue(Option.<Object>of(1).cast(String.class).isEmpty());
	}

	@Test
	void emptyGetThrowsNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> Option.empty().get());
		assertThrows(IllegalStateException.class,
				() -> Option.empty().orElseThrow(() -> new IllegalStateException("Missing value")));
		assertThrows(IllegalArgumentException.class,
				() -> Option.empty().orElseThrow(IllegalArgumentException::new, "Missing value"));
	}
}
