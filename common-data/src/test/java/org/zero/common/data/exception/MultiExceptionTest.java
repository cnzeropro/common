package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/01
 */
class MultiExceptionTest {
	private static final String DEFAULT_MESSAGE = "Multiple exceptions occurred";

	@Test
	void multiExceptionShouldUseSingleThrowableAsCauseAndExposeConsistentMessages() {
		IllegalStateException cause = new IllegalStateException("boom");
		MultiException exception = new MultiException(cause);
		String expectedMessage = DEFAULT_MESSAGE + " (count=1): " + cause;

		assertEquals(expectedMessage, exception.getMessage());
		assertEquals(expectedMessage, exception.getErrorMessage());
		assertEquals(expectedMessage, exception.getLocalizedMessage());
		assertSame(cause, exception.getCause());
		assertEquals(0, exception.getSuppressed().length);
		assertArrayEquals(new Throwable[]{cause}, exception.getThrowables());
	}

	@Test
	void multiExceptionShouldAttachRemainingThrowablesAsSuppressedInOriginalOrder() {
		IllegalStateException cause = new IllegalStateException("primary");
		IllegalArgumentException other = new IllegalArgumentException("other");
		MultiException exception = new MultiException(cause, null, cause, other);

		assertSame(cause, exception.getCause());
		assertArrayEquals(new Throwable[]{cause, cause, other}, exception.getThrowables());
		assertArrayEquals(new Throwable[]{other}, exception.getSuppressed());
	}

	@Test
	void multiExceptionShouldUseExplicitMessageAndFallbackWhenMessageIsBlankOrNull() {
		IllegalStateException cause = new IllegalStateException("boom");
		MultiException explicitMessageException = new MultiException("custom message", cause);
		MultiException blankMessageException = new MultiException("  ", cause);
		MultiException nullMessageException = new MultiException((String) null, cause);
		String expectedGeneratedMessage = DEFAULT_MESSAGE + " (count=1): " + cause;

		assertEquals("custom message", explicitMessageException.getMessage());
		assertEquals("custom message", explicitMessageException.getErrorMessage());
		assertEquals("custom message", explicitMessageException.getLocalizedMessage());

		assertEquals(expectedGeneratedMessage, blankMessageException.getMessage());
		assertEquals(expectedGeneratedMessage, nullMessageException.getMessage());
	}

	@Test
	void multiExceptionShouldHandleNullInputAndNullElementsWithoutFailure() {
		MultiException nullArrayException = new MultiException((Throwable[]) null);
		MultiException nullElementsException = new MultiException(new Throwable[]{null, null});

		assertEquals(DEFAULT_MESSAGE, nullArrayException.getMessage());
		assertEquals(DEFAULT_MESSAGE, nullElementsException.getMessage());
		assertNull(nullArrayException.getCause());
		assertNull(nullElementsException.getCause());
		assertEquals(0, nullArrayException.getSuppressed().length);
		assertEquals(0, nullElementsException.getSuppressed().length);
		assertArrayEquals(new Throwable[0], nullArrayException.getThrowables());
		assertArrayEquals(new Throwable[0], nullElementsException.getThrowables());
	}

	@Test
	void multiExceptionShouldDefensivelyCopyInputAndGetterResults() {
		IllegalStateException first = new IllegalStateException("first");
		IllegalArgumentException second = new IllegalArgumentException("second");
		Throwable[] source = new Throwable[]{first, second};
		MultiException exception = new MultiException(source);
		Throwable[] exposed = exception.getThrowables();

		source[0] = new RuntimeException("changed-source");
		exposed[1] = new RuntimeException("changed-getter");

		Throwable[] actual = exception.getThrowables();
		assertNotSame(source, actual);
		assertNotSame(exposed, actual);
		assertSame(first, actual[0]);
		assertSame(second, actual[1]);
	}
}
