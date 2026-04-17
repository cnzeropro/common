package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
public class DynamicByteArrayInputStreamTest {
	@Test
	void shouldRejectOverflowingReadRange() {
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8));

		assertThrows(IndexOutOfBoundsException.class, () -> inputStream.read(new byte[4], 1, Integer.MAX_VALUE));
	}

	@Test
	void shouldPreserveCursorAcrossMixedReadsSkipsAndReset() throws IOException {
		DynamicByteArrayOutputStream outputStream = new DynamicByteArrayOutputStream(2, new DynamicByteArrayOutputStream.FixedBufferSizeStrategy(2));
		outputStream.write("abcdef".getBytes(StandardCharsets.UTF_8));
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream(outputStream);
		byte[] first = new byte[2];
		byte[] tail = new byte[3];

		assertEquals('a', inputStream.read());
		assertEquals(2, inputStream.read(first));
		assertArrayEquals("bc".getBytes(StandardCharsets.UTF_8), first);
		inputStream.mark(16);
		assertEquals(2L, inputStream.skip(2));
		assertEquals('f', inputStream.read());
		inputStream.reset();
		assertEquals(3, inputStream.read(tail));
		assertArrayEquals("def".getBytes(StandardCharsets.UTF_8), tail);
		assertEquals(0, inputStream.remaining().intValue());
	}

	@Test
	void shouldKeepRemainingReadableAfterClose() throws IOException {
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8));

		assertEquals('a', inputStream.read());
		inputStream.close();

		assertThrows(IOException.class, inputStream::available);
		assertEquals(2, inputStream.remaining().intValue());
	}

	@Test
	void shouldIgnoreMarkAfterCloseButStillRejectOtherOperations() {
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream(Collections.singletonList(new byte[]{1, 2, 3}));
		inputStream.close();

		inputStream.mark(4);

		assertThrows(IOException.class, inputStream::reset);
		assertThrows(IOException.class, () -> inputStream.read());
		assertThrows(IOException.class, () -> inputStream.skip(1));
	}
}
