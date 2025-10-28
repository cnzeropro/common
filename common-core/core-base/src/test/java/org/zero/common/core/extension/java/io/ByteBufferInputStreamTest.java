package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.io.IoUtil;

import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
class ByteBufferInputStreamTest {
	@Test
	void test() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.put("Hello World".getBytes());
		byteBuffer.flip();
		ByteBufferInputStream inputStream = new ByteBufferInputStream(byteBuffer);
		byte[] bytes = IoUtil.readAll(inputStream);
		System.out.println(new String(bytes));
	}
}
