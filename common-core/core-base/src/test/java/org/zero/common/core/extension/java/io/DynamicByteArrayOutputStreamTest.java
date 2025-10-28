package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.util.RandomUtil;

import java.io.IOException;
import java.util.Random;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/28
 */
public class DynamicByteArrayOutputStreamTest {
	@Test
	 void test() throws IOException {
		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DynamicByteArrayOutputStream outputStream = new DynamicByteArrayOutputStream();
		Random random = RandomUtil.getRandom();
		for (int i = 0; i < 1000; i++) {
			byte[] bytes = new byte[10000];
			random.nextBytes(bytes);
			outputStream.write(bytes);
		}
		System.out.println(outputStream.size());
		ChunkedByteBuffer chunkedByteBuffer = outputStream.toChunkedByteBuffer(777);
		DynamicByteArrayInputStream inputStream = new DynamicByteArrayInputStream(chunkedByteBuffer);
		System.out.println(inputStream.remaining());
		byte[] bytes = new byte[10000];
		inputStream.read(bytes);
		System.out.println(inputStream.remaining());
	}
}
