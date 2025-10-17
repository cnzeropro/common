package org.zero.common.core.extension.java.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class ByteBufferOutputStream extends OutputStream {
	protected ByteBuffer byteBuffer;

	public ByteBufferOutputStream(int bufferSize) {
		this.byteBuffer = ByteBuffer.allocate(bufferSize);
	}

	@Override
	public void write(int b) throws IOException {
		if (!byteBuffer.hasRemaining()) {
			flush();
		}
		byteBuffer.put((byte) b);
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
		if (byteBuffer.remaining() < length) {
			flush();
		}
		byteBuffer.put(bytes, offset, length);
	}
}
