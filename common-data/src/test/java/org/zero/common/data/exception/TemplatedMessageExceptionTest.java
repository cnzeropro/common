package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;
import org.zero.common.data.format.HutoolMessageFormatter;
import org.zero.common.data.format.Slf4jMessageFormatter;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class TemplatedMessageExceptionTest {

	@Test
	void templatedMessageExceptionShouldUseDefaultFormatterByDefault() {
		TemplatedMessageException exception = new TemplatedMessageException(new StringBuilder("user {0} at {1}"), "zero", 3);

		assertEquals("user zero at 3", exception.getMessage());
	}

	@Test
	void templatedMessageExceptionShouldTreatStringConstructorAsPlainMessage() {
		TemplatedMessageException exception = new TemplatedMessageException("user {0}");

		assertEquals("user {0}", exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldAdaptThrowableOnlyConstructorToRuntimeExceptionBehavior() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception = new TemplatedMessageException(cause);

		assertEquals(cause.toString(), exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldTreatMessageAndCauseConstructorAsPlainMessage() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception = new TemplatedMessageException("user {0}", cause);

		assertEquals("user {0}", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldAdaptProtectedRuntimeExceptionFlagsConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception = new TemplatedMessageException("user {0}", cause, false, false);

		exception.addSuppressed(new IllegalArgumentException("suppressed"));

		assertEquals("user {0}", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(0, exception.getSuppressed().length);
		assertEquals(0, exception.getStackTrace().length);
	}

	@Test
	void templatedMessageExceptionShouldTreatTrailingThrowableAsImplicitCauseByDefaultFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception = new TemplatedMessageException("user {0}", "zero", cause);

		assertEquals("user zero", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldUseExplicitCauseAndLocale() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception =
			new TemplatedMessageException(cause, "amount {0,number,#,##0.00}", Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldUseProvidedSlf4jFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception =
			new TemplatedMessageException(cause, Slf4jMessageFormatter.INSTANCE, new StringBuilder("user {}"), "zero");

		assertEquals("user zero", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldPreferExplicitCauseOverFormatterResult() {
		IllegalStateException explicitCause = new IllegalStateException("outer");
		IllegalArgumentException implicitCause = new IllegalArgumentException("inner");
		TemplatedMessageException exception = new TemplatedMessageException(
			explicitCause,
			Slf4jMessageFormatter.INSTANCE,
			"user {} failed",
			"zero",
			implicitCause
		);

		assertEquals("user zero failed", exception.getMessage());
		assertSame(explicitCause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldFallbackToDefaultFormatterWhenFormatterIsNull() {
		TemplatedMessageException exception =
			new TemplatedMessageException((org.zero.common.data.format.MessageFormatter) null, "user {0}", "zero");

		assertEquals("user zero", exception.getMessage());
	}

	@Test
	void templatedMessageExceptionShouldSupportDirectParamTupleConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException.ParamTuple paramTuple = new TemplatedMessageException.ParamTuple(
			cause,
			null,
			new StringBuffer("amount {0,number,#,##0.00}"),
			Locale.GERMANY,
			new Object[]{1234.56d}
		);
		TemplatedMessageException exception = new TemplatedMessageException(paramTuple);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldSupportBuilderParamTupleConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException.ParamTuple paramTuple = TemplatedMessageException.ParamTuple.builder()
			.formatter(Slf4jMessageFormatter.INSTANCE)
			.pattern(new StringBuilder("user {} failed"))
			.args(new Object[]{"zero", cause})
			.build();
		TemplatedMessageException exception = new TemplatedMessageException(paramTuple);

		assertEquals("user zero failed", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldSupportCharSequencePatternWithLocale() {
		TemplatedMessageException exception =
			new TemplatedMessageException(new StringBuffer("amount {0,number,#,##0.00}"), Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
	}

	@Test
	void templatedMessageExceptionShouldUseProvidedHutoolFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException exception =
			new TemplatedMessageException(cause, HutoolMessageFormatter.INSTANCE, new StringBuilder("user {}"), "zero");

		assertEquals("user {}", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldPreferExplicitCauseOverHutoolImplicitCause() {
		IllegalStateException explicitCause = new IllegalStateException("outer");
		IllegalArgumentException implicitCause = new IllegalArgumentException("inner");
		TemplatedMessageException exception = new TemplatedMessageException(
			explicitCause,
			HutoolMessageFormatter.INSTANCE,
			"user {} failed",
			"zero",
			implicitCause
		);

		assertEquals("user {} failed", exception.getMessage());
		assertSame(explicitCause, exception.getCause());
	}

	@Test
	void templatedMessageExceptionShouldSupportHutoolFormatterInParamTuple() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException.ParamTuple paramTuple = TemplatedMessageException.ParamTuple.builder()
			.formatter(HutoolMessageFormatter.INSTANCE)
			.pattern(new StringBuilder("user {} failed"))
			.args(new Object[]{"zero", cause})
			.build();
		TemplatedMessageException exception = new TemplatedMessageException(paramTuple);

		assertEquals("user {} failed", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
}
