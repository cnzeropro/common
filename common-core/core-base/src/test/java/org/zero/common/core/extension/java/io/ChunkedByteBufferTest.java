package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/24
 */
class ChunkedByteBufferTest {
	@Test
	void shouldSupportInsertDeleteReplaceAndGet() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);

		buffer.put("helloworld!".getBytes(StandardCharsets.UTF_8));
		buffer.insert(BigInteger.valueOf(5), " ".getBytes(StandardCharsets.UTF_8));
		buffer.replace(BigInteger.valueOf(6), "W".getBytes(StandardCharsets.UTF_8));
		buffer.delete(BigInteger.valueOf(11), BigInteger.ONE);

		assertEquals(BigInteger.valueOf(11), buffer.size());
		assertEquals("hello World", new String(buffer.get(11), StandardCharsets.UTF_8));
		assertEquals("World", new String(buffer.get(BigInteger.valueOf(6), 5), StandardCharsets.UTF_8));
		assertTrue(buffer.blockSize() > 1);
	}

	@Test
	void shouldCopyWithoutSharingContent() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);
		buffer.put("abcdef".getBytes(StandardCharsets.UTF_8));

		ChunkedByteBuffer copied = buffer.copy(3);

		assertTrue(copied.contentEquals(buffer));

		copied.insert(BigInteger.ZERO, "z".getBytes(StandardCharsets.UTF_8));

		assertFalse(copied.contentEquals(buffer));
		assertEquals("abcdef", new String(buffer.get(6), StandardCharsets.UTF_8));
		assertEquals("zabcdef", new String(copied.get(7), StandardCharsets.UTF_8));
	}

	@Test
	void shouldReadFromAndWriteToStreams() throws IOException {
		byte[] source = "0123456789".getBytes(StandardCharsets.UTF_8);
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(4);

		BigInteger bytesRead = buffer.readFrom(
				new ByteArrayInputStream(source),
				BigInteger.valueOf(2),
				BigInteger.valueOf(5),
				true
		);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		buffer.writeTo(outputStream);

		assertEquals(BigInteger.valueOf(5), bytesRead);
		assertArrayEquals("23456".getBytes(StandardCharsets.UTF_8), buffer.get(5));
		assertArrayEquals("23456".getBytes(StandardCharsets.UTF_8), outputStream.toByteArray());
	}
}
