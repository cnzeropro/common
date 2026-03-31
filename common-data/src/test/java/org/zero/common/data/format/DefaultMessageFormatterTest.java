package org.zero.common.data.format;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class DefaultMessageFormatterTest {

	@Test
	void defaultMessageFormatterShouldFormatMessagePattern() {
		String message = DefaultMessageFormatter.INSTANCE.format(new StringBuilder("user {0} at {1}"), (Locale) null, "zero", 3);

		assertEquals("user zero at 3", message);
	}

	@Test
	void defaultMessageFormatterShouldApplyLocale() {
		String message = DefaultMessageFormatter.INSTANCE.format("amount {0,number,#,##0.00}", Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", message);
	}

	@Test
	void defaultMessageFormatterShouldMatchFormatFailureBehavior() {
		CharSequence pattern = new StringBuffer("user {0");
		String message = DefaultMessageFormatter.INSTANCE.format(pattern, Locale.US, "zero");

		assertEquals(pattern.toString(), message);
	}

	@Test
	void defaultMessageFormatterShouldTreatTrailingThrowableAsNormalArgument() {
		IllegalStateException cause = new IllegalStateException("boom");
		String message = DefaultMessageFormatter.INSTANCE.format("user {0} caused by {1}", (Locale) null, "zero", cause);

		assertEquals("user zero caused by " + cause, message);
	}

	@Test
	void defaultMessageFormatterShouldKeepNullPatternBehavior() {
		String message = DefaultMessageFormatter.INSTANCE.format((CharSequence) null, Locale.US, "zero");

		assertNull(message);
	}
}
