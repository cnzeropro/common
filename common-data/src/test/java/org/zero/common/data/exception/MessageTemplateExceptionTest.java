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
class MessageTemplateExceptionTest {

	@Test
	void messageTemplateExceptionShouldUseDefaultFormatterByDefault() {
		MessageTemplateException exception = new MessageTemplateException(new StringBuilder("user {0} at {1}"), "zero", 3);

		assertEquals("user zero at 3", exception.getMessage());
	}

	@Test
	void messageTemplateExceptionShouldTreatStringConstructorAsPlainMessage() {
		MessageTemplateException exception = new MessageTemplateException("user {0}");

		assertEquals("user {0}", exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldAdaptThrowableOnlyConstructorToRuntimeExceptionBehavior() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception = new MessageTemplateException(cause);

		assertEquals(cause.toString(), exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldTreatMessageAndCauseConstructorAsPlainMessage() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception = new MessageTemplateException("user {0}", cause);

		assertEquals("user {0}", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldAdaptProtectedRuntimeExceptionFlagsConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception = new MessageTemplateException("user {0}", cause, false, false);

		exception.addSuppressed(new IllegalArgumentException("suppressed"));

		assertEquals("user {0}", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(0, exception.getSuppressed().length);
		assertEquals(0, exception.getStackTrace().length);
	}

	@Test
	void messageTemplateExceptionShouldTreatTrailingThrowableAsImplicitCauseByDefaultFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception = new MessageTemplateException("user {0}", "zero", cause);

		assertEquals("user zero", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldUseExplicitCauseAndLocale() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception =
			new MessageTemplateException(cause, "amount {0,number,#,##0.00}", Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldUseProvidedSlf4jFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception =
			new MessageTemplateException(cause, Slf4jMessageFormatter.INSTANCE, new StringBuilder("user {}"), "zero");

		assertEquals("user zero", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldPreferExplicitCauseOverFormatterResult() {
		IllegalStateException explicitCause = new IllegalStateException("outer");
		IllegalArgumentException implicitCause = new IllegalArgumentException("inner");
		MessageTemplateException exception = new MessageTemplateException(
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
	void messageTemplateExceptionShouldFallbackToDefaultFormatterWhenFormatterIsNull() {
		MessageTemplateException exception =
			new MessageTemplateException((org.zero.common.data.format.MessageFormatter) null, "user {0}", "zero");

		assertEquals("user zero", exception.getMessage());
	}

	@Test
	void messageTemplateExceptionShouldSupportDirectParamTupleConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException.ParamTuple paramTuple = new MessageTemplateException.ParamTuple(
			cause,
			null,
			new StringBuffer("amount {0,number,#,##0.00}"),
			Locale.GERMANY,
			new Object[]{1234.56d}
		);
		MessageTemplateException exception = new MessageTemplateException(paramTuple);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldSupportBuilderParamTupleConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException.ParamTuple paramTuple = MessageTemplateException.ParamTuple.builder()
			.formatter(Slf4jMessageFormatter.INSTANCE)
			.pattern(new StringBuilder("user {} failed"))
			.args(new Object[]{"zero", cause})
			.build();
		MessageTemplateException exception = new MessageTemplateException(paramTuple);

		assertEquals("user zero failed", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldSupportCharSequencePatternWithLocale() {
		MessageTemplateException exception =
			new MessageTemplateException(new StringBuffer("amount {0,number,#,##0.00}"), Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
	}

	@Test
	void messageTemplateExceptionShouldUseProvidedHutoolFormatter() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException exception =
			new MessageTemplateException(cause, HutoolMessageFormatter.INSTANCE, new StringBuilder("user {}"), "zero");

		assertEquals("user {}", exception.getMessage());
		assertSame(cause, exception.getCause());
	}

	@Test
	void messageTemplateExceptionShouldPreferExplicitCauseOverHutoolImplicitCause() {
		IllegalStateException explicitCause = new IllegalStateException("outer");
		IllegalArgumentException implicitCause = new IllegalArgumentException("inner");
		MessageTemplateException exception = new MessageTemplateException(
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
	void messageTemplateExceptionShouldSupportHutoolFormatterInParamTuple() {
		IllegalStateException cause = new IllegalStateException("boom");
		MessageTemplateException.ParamTuple paramTuple = MessageTemplateException.ParamTuple.builder()
			.formatter(HutoolMessageFormatter.INSTANCE)
			.pattern(new StringBuilder("user {} failed"))
			.args(new Object[]{"zero", cause})
			.build();
		MessageTemplateException exception = new MessageTemplateException(paramTuple);

		assertEquals("user {} failed", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
}
