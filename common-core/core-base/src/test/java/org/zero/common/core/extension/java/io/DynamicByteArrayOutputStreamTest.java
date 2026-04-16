package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/28
 */
public class DynamicByteArrayOutputStreamTest {
	@Test
	void shouldStopAtLogicalEofWhenReadingSingleBytes() throws IOException {
		DynamicByteArrayOutputStream outputStream = new DynamicByteArrayOutputStream(8, new DynamicByteArrayOutputStream.FixedBufferSizeStrategy(8));
		byte[] expected = "hello".getBytes(StandardCharsets.UTF_8);
		outputStream.write(expected);
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream(outputStream);

		for (byte value : expected) {
			assertEquals(value & 0xFF, inputStream.read());
		}

		assertEquals(-1, inputStream.read());
		assertEquals(0, inputStream.remaining().intValue());
	}

	@Test
	void shouldRespectLogicalSizeAcrossMixedReads() throws IOException {
		DynamicByteArrayOutputStream outputStream = new DynamicByteArrayOutputStream(4, new DynamicByteArrayOutputStream.FixedBufferSizeStrategy(4));
		outputStream.write("abcdef".getBytes(StandardCharsets.UTF_8));
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream(outputStream);
		byte[] first = new byte[3];
		byte[] second = new byte[4];

		assertEquals(3, inputStream.read(first));
		assertEquals('d', inputStream.read());
		assertEquals(2, inputStream.read(second));
		assertEquals(-1, inputStream.read());

		assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), first);
		assertArrayEquals(new byte[]{'e', 'f', 0, 0}, second);
		assertEquals(0, inputStream.remaining().intValue());
	}
}
