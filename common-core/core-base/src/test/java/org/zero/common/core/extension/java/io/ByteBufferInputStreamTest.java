package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
class ByteBufferInputStreamTest {
	@Test
	void shouldReadBytesWithOffsets() throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.wrap("Hello World".getBytes(StandardCharsets.UTF_8));
		ByteBufferInputStream inputStream = new ByteBufferInputStream(byteBuffer);
		byte[] target = new byte[6];

		assertEquals(5, inputStream.read(target, 1, 5));
		assertArrayEquals(new byte[]{0, 'H', 'e', 'l', 'l', 'o'}, target);
		assertEquals(6, inputStream.available());
		assertEquals(' ', inputStream.read());
	}

	@Test
	void shouldSupportSkipMarkAndReset() throws IOException {
		ByteBufferInputStream inputStream = new ByteBufferInputStream(ByteBuffer.wrap("abcdef".getBytes(StandardCharsets.UTF_8)));

		assertEquals('a', inputStream.read());
		inputStream.mark(0);
		assertEquals('b', inputStream.read());
		assertEquals(2, inputStream.skip(2));
		assertEquals('e', inputStream.read());
		inputStream.reset();
		assertEquals('b', inputStream.read());
		assertEquals(4, inputStream.available());
	}

	@Test
	void shouldReturnMinusOneAtEofAndRejectReadsAfterClose() throws IOException {
		ByteBufferInputStream inputStream = new ByteBufferInputStream(ByteBuffer.wrap("ab".getBytes(StandardCharsets.UTF_8)));

		assertEquals('a', inputStream.read());
		assertEquals('b', inputStream.read());
		assertEquals(-1, inputStream.read());

		inputStream.close();

		assertThrows(IOException.class, inputStream::read);
	}

	@Test
	void shouldThrowIOExceptionWhenResetWithoutMark() {
		ByteBufferInputStream inputStream = new ByteBufferInputStream(ByteBuffer.wrap("ab".getBytes(StandardCharsets.UTF_8)));

		IOException exception = assertThrows(IOException.class, inputStream::reset);

		assertEquals("Mark not set", exception.getMessage());
	}
}
