package org.zero.common.core.extension.java.io;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class ByteBufferInputStream extends InputStream {
	protected final ByteBuffer byteBuffer;
	@Getter
	protected volatile boolean closed = false;

	/**
	 * 使用 {@link ByteBuffer} 构造
	 * <p>
	 * 注意：传入的 {@link ByteBuffer} 需要先调用 {@linkplain ByteBuffer#flip() flip} 方法切换为读模式。
	 *
	 * @param byteBuffer 字节缓冲区
	 */
	public ByteBufferInputStream(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	@Override
	public synchronized int read() throws IOException {
		checkClosed();
		if (!byteBuffer.hasRemaining()) {
			return -1;
		}
		// 无符号转换
		return byteBuffer.get() & 0xFF;
	}

	@Override
	public synchronized int read(byte[] bytes) throws IOException {
		if (bytes == null) {
			throw new NullPointerException("Byte array cannot be null");
		}
		return read(bytes, 0, bytes.length);
	}

	@Override
	public synchronized int read(byte[] bytes, int offset, int length) throws IOException {
		checkClosed();
		if (bytes == null) {
			throw new NullPointerException("Byte array cannot be null");
		}
		if (offset < 0 || length < 0 || length > bytes.length - offset) {
			throw new IndexOutOfBoundsException(String.format("offset=%d, length=%d, arrayLength=%d", offset, length, bytes.length));
		}
		if (length == 0) {
			return 0;
		}
		int remaining = byteBuffer.remaining();
		if (remaining == 0) {
			return -1;
		}
		int count = Math.min(remaining, length);
		byteBuffer.get(bytes, offset, count);
		return count;
	}

	@Override
	public synchronized int available() throws IOException {
		checkClosed();
		return byteBuffer.remaining();
	}

	@Override
	public synchronized long skip(long n) throws IOException {
		checkClosed();
		if (n <= 0) {
			return 0;
		}
		int skip = (int) Math.min(byteBuffer.remaining(), n);
		byteBuffer.position(byteBuffer.position() + skip);
		return skip;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@SneakyThrows
	@Override
	public synchronized void mark(int readlimit) {
		checkClosed();
		byteBuffer.mark();
	}

	@Override
	public synchronized void reset() throws IOException {
		checkClosed();
		try {
			byteBuffer.reset();
		} catch (InvalidMarkException exception) {
			throw new IOException("Mark not set", exception);
		}
	}

	@Override
	public void close() throws IOException {
		closed = true;
	}

	protected void checkClosed() throws IOException {
		if (isClosed()) {
			throw new IOException(this.getClass() + " is closed");
		}
	}
}
