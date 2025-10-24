package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/24
 */
class ChunkedByteBufferTest {
	@Test
	void test() {
		// 注意：同等数据总量下，maxBlockSize 越小，性能越低
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);
		buffer.put("hello".getBytes());
		buffer.put("world".getBytes());
		buffer.put("!".getBytes());
		buffer.insert(BigInteger.valueOf(5), " ".getBytes());
		buffer.put(new byte[100000]);
		buffer.delete(1000, 1000);
		byte[] bytes = new byte[1000];
		Arrays.fill(bytes, (byte) 11);
		buffer.replace(100, bytes);
		ChunkedByteBuffer copied = buffer.copy(888);
		System.out.println(buffer.size());
		System.out.println(new String(buffer.get(12)));
		System.out.println(copied.contentEquals(buffer));
	}

	@Test
	void test1() throws IOException {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer();
		buffer.readFrom(Files.newInputStream(Paths.get("C:\\Users\\Rongan\\Downloads\\zh-cn_windows_11_business_editions_version_25h2_x64_dvd_22759158.iso")));
		System.out.println(buffer.size());
		// buffer.delete(1000, 1);
		buffer.writeTo(Files.newOutputStream(Paths.get("C:\\Users\\Rongan\\Desktop\\zh-cn_windows_11_business_editions_version_25h2_x64_dvd_22759158.iso")));
	}
}
