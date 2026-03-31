package org.zero.common.data.format;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/27
 */
class HutoolMessageFormatterTest {

	@Test
	void hutoolMessageFormatterShouldFormatPlaceholders() {
		String message = HutoolMessageFormatter.INSTANCE.format(new StringBuilder("user {} at {}"), (Locale) null, "zero", 3);

		assertEquals("user {} at [zero, 3]", message);
	}

	@Test
	void hutoolMessageFormatterShouldTreatTrailingThrowableAsNormalArgument() {
		IllegalStateException cause = new IllegalStateException("boom");
		String message = HutoolMessageFormatter.INSTANCE.format(new StringBuffer("user {} caused by {}"), Locale.CHINA, "zero", cause);

		assertEquals("user {} caused by [zero, " + cause + "]", message);
	}

	@Test
	void hutoolMessageFormatterShouldIgnoreLocaleWhenNoThrowableProvided() {
		String message = HutoolMessageFormatter.INSTANCE.format(new StringBuffer("value {}"), Locale.GERMANY, 1234.56d);

		assertEquals("value {}", message);
	}

	@Test
	void hutoolMessageFormatterShouldUseHutoolEscapeRules() {
		String message = HutoolMessageFormatter.INSTANCE.format("\\{} and {}", Locale.US, "value");

		assertEquals("{} and {}", message);
	}
}
