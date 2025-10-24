package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
class ByteBufferOutputStreamTest {
	@Test
	void test() throws IOException {
		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(1,13);
		outputStream.write("Hello, World!".getBytes());
		byte[] byteArray = outputStream.toByteArray();
		System.out.println(new String(byteArray));
	}
}
