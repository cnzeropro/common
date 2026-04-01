package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Status;
import org.zero.common.data.format.MessageFormatter;
import org.zero.common.data.format.Slf4jMessageFormatter;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class BaseExceptionTest {

	@Test
	void baseExceptionShouldUseDefaultStatusWhenNoArgConstructorIsUsed() {
		BaseException exception = new BaseException();

		assertEquals("error", exception.getMessage());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getErrorMessage());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldUseProvidedStatusConstructor() {
		Status status = Status.Default.of("E100", "business error");
		BaseException exception = new BaseException(status);

		assertEquals("business error", exception.getMessage());
		assertEquals("E100", exception.getErrorCode());
		assertEquals("business error", exception.getErrorMessage());
		assertEquals("business error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldFallbackToDefaultStatusWhenNullStatusIsProvided() {
		BaseException statusException = new BaseException((Status) null);
		BaseException messageStatusException = new BaseException("plain", (Status) null);
		BaseException causeStatusException = new BaseException(new IllegalStateException("boom"), (Status) null);
		BaseException templatedStatusException = new BaseException((Status) null, "user {0}", "zero");

		assertEquals("error", statusException.getMessage());
		assertEquals(Status.ERROR_CODE, statusException.getErrorCode());
		assertEquals("error", statusException.getLocalizedMessage());

		assertEquals("plain", messageStatusException.getMessage());
		assertEquals(Status.ERROR_CODE, messageStatusException.getErrorCode());
		assertEquals("error", messageStatusException.getLocalizedMessage());

		assertEquals("java.lang.IllegalStateException: boom", causeStatusException.getMessage());
		assertEquals(Status.ERROR_CODE, causeStatusException.getErrorCode());
		assertEquals("error", causeStatusException.getLocalizedMessage());

		assertEquals("user zero", templatedStatusException.getMessage());
		assertEquals(Status.ERROR_CODE, templatedStatusException.getErrorCode());
		assertEquals("error", templatedStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportMessageConstructors() {
		Status status = Status.Default.of("E200", "legacy");
		BaseException messageException = new BaseException("plain");
		BaseException messageStatusException = new BaseException("plain", status);

		assertEquals("plain", messageException.getMessage());
		assertEquals(Status.ERROR_CODE, messageException.getErrorCode());
		assertEquals("error", messageException.getLocalizedMessage());

		assertEquals("plain", messageStatusException.getMessage());
		assertEquals("E200", messageStatusException.getErrorCode());
		assertEquals("legacy", messageStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldTreatStringConstructorAsPlainMessageEvenWhenMessageLooksLikeTemplate() {
		BaseException exception = new BaseException("user {0}");

		assertEquals("user {0}", exception.getMessage());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportCauseConstructors() {
		Status status = Status.Default.of("E300", "legacy cause");
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException causeException = new BaseException(cause);
		BaseException causeStatusException = new BaseException(cause, status);

		assertEquals(cause.toString(), causeException.getMessage());
		assertSame(cause, causeException.getCause());
		assertEquals(Status.ERROR_CODE, causeException.getErrorCode());
		assertEquals("error", causeException.getLocalizedMessage());

		assertEquals(cause.toString(), causeStatusException.getMessage());
		assertSame(cause, causeStatusException.getCause());
		assertEquals("E300", causeStatusException.getErrorCode());
		assertEquals("legacy cause", causeStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportMessageAndCauseConstructors() {
		Status status = Status.Default.of("E400", "legacy message cause");
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException messageCauseException = new BaseException("plain", cause);
		BaseException messageCauseStatusException = new BaseException("plain", cause, status);

		assertEquals("plain", messageCauseException.getMessage());
		assertSame(cause, messageCauseException.getCause());
		assertEquals(Status.ERROR_CODE, messageCauseException.getErrorCode());
		assertEquals("error", messageCauseException.getLocalizedMessage());

		assertEquals("plain", messageCauseStatusException.getMessage());
		assertSame(cause, messageCauseStatusException.getCause());
		assertEquals("E400", messageCauseStatusException.getErrorCode());
		assertEquals("legacy message cause", messageCauseStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportTemplatedConstructorWithDefaultStatus() {
		BaseException exception = new BaseException(new StringBuilder("user {0} at {1}"), "zero", 3);

		assertEquals("user zero at 3", exception.getMessage());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getErrorMessage());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportTemplatedConstructorWithProvidedStatus() {
		Status status = Status.Default.of("E500", "templated business error");
		BaseException exception = new BaseException(status, new StringBuilder("user {0} at {1}"), "zero", 3);

		assertEquals("user zero at 3", exception.getMessage());
		assertEquals("E500", exception.getErrorCode());
		assertEquals("templated business error", exception.getErrorMessage());
		assertEquals("templated business error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportTemplatedConstructorWithLocale() {
		Status status = Status.Default.of("E510", "locale business error");
		BaseException exception =
			new BaseException(status, new StringBuffer("amount {0,number,#,##0.00}"), Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertEquals("E510", exception.getErrorCode());
		assertEquals("locale business error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportTemplatedConstructorWithExplicitCauseAndStatus() {
		Status status = Status.Default.of("E520", "explicit cause error");
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException exception =
			new BaseException(status, cause, "amount {0,number,#,##0.00}", Locale.GERMANY, 1234.56d);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals("E520", exception.getErrorCode());
		assertEquals("explicit cause error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportTemplatedConstructorWithFormatter() {
		Status status = Status.Default.of("E530", "formatter business error");
		BaseException exception =
			new BaseException(status, Slf4jMessageFormatter.INSTANCE, new StringBuilder("user {}"), "zero");

		assertEquals("user zero", exception.getMessage());
		assertEquals("E530", exception.getErrorCode());
		assertEquals("formatter business error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldFallbackToDefaultFormatterWhenFormatterIsNull() {
		BaseException exception = new BaseException((MessageFormatter) null, "user {0}", "zero");

		assertEquals("user zero", exception.getMessage());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldTreatTrailingThrowableAsImplicitCauseInTemplateConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException exception = new BaseException("user {0}", "zero", cause);

		assertEquals("user zero", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportDefaultStatusParamTupleConstructor() {
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException.ParamTuple paramTuple = TemplatedMessageException.ParamTuple.builder()
			.formatter(Slf4jMessageFormatter.INSTANCE)
			.pattern(new StringBuilder("user {} failed"))
			.args(new Object[]{"zero", cause})
			.build();
		BaseException exception = new BaseException(paramTuple);

		assertEquals("user zero failed", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportProvidedStatusParamTupleConstructor() {
		Status status = Status.Default.of("E540", "tuple business error");
		IllegalStateException cause = new IllegalStateException("boom");
		TemplatedMessageException.ParamTuple paramTuple = new TemplatedMessageException.ParamTuple(
			cause,
			null,
			new StringBuffer("amount {0,number,#,##0.00}"),
			Locale.GERMANY,
			new Object[]{1234.56d}
		);
		BaseException exception = new BaseException(status, paramTuple);

		assertEquals("amount 1.234,56", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals("E540", exception.getErrorCode());
		assertEquals("tuple business error", exception.getLocalizedMessage());
	}

	@Test
	void standardExceptionSubclassesShouldRemainCompatible() {
		IllegalStateException cause = new IllegalStateException("boom");
		CommonException commonException = new CommonException("plain");
		UtilException utilException = new UtilException(cause);

		assertEquals("plain", commonException.getMessage());
		assertEquals("error", commonException.getLocalizedMessage());

		assertEquals("boom", utilException.getMessage());
		assertSame(cause, utilException.getCause());
		assertEquals("error", utilException.getLocalizedMessage());
	}
}
