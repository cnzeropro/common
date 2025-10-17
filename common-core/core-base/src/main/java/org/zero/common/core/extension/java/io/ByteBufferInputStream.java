package org.zero.common.core.extension.java.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class ByteBufferInputStream extends InputStream {
	protected ByteBuffer byteBuffer;

	public ByteBufferInputStream(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	@Override
	public int read() throws IOException {
		if (!byteBuffer.hasRemaining()) {
			return -1;
		}
		// 无符号转换
		return byteBuffer.get() & 0xFF;
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		if (length == 0) {
			return 0;
		}
		int count = Math.min(byteBuffer.remaining(), length);
		if (count == 0) {
			return -1;
		}
		byteBuffer.get(bytes, offset, count);
		return count;
	}

	@Override
	public int available() throws IOException {
		return byteBuffer.remaining();
	}
}
