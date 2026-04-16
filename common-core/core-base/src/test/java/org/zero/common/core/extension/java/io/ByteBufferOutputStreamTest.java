package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
class ByteBufferOutputStreamTest {
	@Test
	void shouldExpandAndPreserveData() throws IOException {
		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(1, 130);
		byte[] expected = "Hello, World!".getBytes(StandardCharsets.UTF_8);

		outputStream.write(expected);

		assertArrayEquals(expected, outputStream.toByteArray());
		assertEquals(expected.length, outputStream.size());
	}

	@Test
	void shouldFailWhenMaxBufferSizeCannotFitRequestedWrite() {
		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(4, 4);

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> outputStream.write(new byte[5]));

		assertTrue(exception.getMessage().contains("oldCapacity=4"));
		assertTrue(exception.getMessage().contains("required=5"));
	}

	@Test
	void shouldFailFastWhenStrategyDoesNotGrowCapacity() throws IOException {
		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(1, (oldSize, requiredSize) -> oldSize);
		outputStream.write(1);

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> outputStream.write(2));

		assertTrue(exception.getMessage().contains("nextCapacity=1"));
	}

	@Test
	void shouldRejectInvalidRangeBeforeExpansion() {
		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(1, (oldSize, requiredSize) -> {
			throw new AssertionError("Buffer expansion should not be triggered for invalid ranges");
		});

		assertThrows(IndexOutOfBoundsException.class, () -> outputStream.write(new byte[1], Integer.MAX_VALUE, 2));
	}

	@Test
	void shouldRejectInvalidConstructorArguments() {
		assertThrows(IllegalArgumentException.class, () -> new ByteBufferOutputStream(-1));
		assertThrows(IllegalArgumentException.class, () -> new ByteBufferOutputStream(2, 1));
		assertThrows(IllegalArgumentException.class, () -> new ByteBufferOutputStream(1, 0));
		assertThrows(NullPointerException.class, () -> new ByteBufferOutputStream((ByteBuffer) null));
		assertThrows(NullPointerException.class, () -> new ByteBufferOutputStream(1, null));
		assertThrows(NullPointerException.class, () -> new ByteBufferOutputStream(ByteBuffer.allocate(1), null));
	}
}
