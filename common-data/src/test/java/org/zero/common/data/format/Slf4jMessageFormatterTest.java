package org.zero.common.data.format;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class Slf4jMessageFormatterTest {

	@Test
	void slf4jMessageFormatterShouldFormatPlaceholders() {
		String message = Slf4jMessageFormatter.INSTANCE.format(new StringBuilder("user {} at {}"), (Locale) null, "zero", 3);

		assertEquals("user zero at 3", message);
	}

	@Test
	void slf4jMessageFormatterShouldTreatTrailingThrowableAsNormalArgument() {
		IllegalStateException cause = new IllegalStateException("boom");
		String message = Slf4jMessageFormatter.INSTANCE.format("user {} caused by {}", Locale.CHINA, "zero", cause);

		assertEquals("user zero caused by " + cause, message);
	}

	@Test
	void slf4jMessageFormatterShouldIgnoreLocaleWhenNoThrowableProvided() {
		String message = Slf4jMessageFormatter.INSTANCE.format("value {}", Locale.GERMANY, 1234.56d);

		assertEquals("value 1234.56", message);
	}
}
