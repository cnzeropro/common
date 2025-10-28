package org.zero.common.core.extension.java.io;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class ByteBufferOutputStream extends OutputStream {
	protected ByteBuffer byteBuffer;
	@Getter
	protected volatile boolean closed = false;
	protected final BufferSizeStrategy bufferSizeStrategy;

	public ByteBufferOutputStream(int initBufferSize) {
		this(initBufferSize, DefaultBufferSizeStrategy.DEFAULT);
	}

	public ByteBufferOutputStream(int initBufferSize, int maxBufferSize) {
		this(initBufferSize, new DefaultBufferSizeStrategy(maxBufferSize));
	}

	public ByteBufferOutputStream(int initBufferSize, BufferSizeStrategy bufferSizeStrategy) {
		this(ByteBuffer.allocate(initBufferSize), bufferSizeStrategy);
	}

	public ByteBufferOutputStream(ByteBuffer byteBuffer) {
		this(byteBuffer, DefaultBufferSizeStrategy.DEFAULT);
	}

	public ByteBufferOutputStream(ByteBuffer byteBuffer, BufferSizeStrategy bufferSizeStrategy) {
		if (byteBuffer.isReadOnly()) {
			throw new IllegalArgumentException("ByteBuffer is read-only");
		}
		if (byteBuffer.position() != 0) {
			throw new IllegalArgumentException("ByteBuffer position must be 0");
		}
		this.byteBuffer = byteBuffer;
		this.bufferSizeStrategy = bufferSizeStrategy;
	}

	@Override
	public synchronized void write(int b) throws IOException {
		checkClosed();
		ensureCapacity(1);
		byteBuffer.put((byte) b);
	}

	@Override
	public synchronized void write(byte[] bytes, int offset, int length) throws IOException {
		checkClosed();
		if (bytes == null) {
			throw new NullPointerException("Byte array cannot be null");
		}
		if (offset < 0 || length < 0 || offset + length > bytes.length) {
			throw new IndexOutOfBoundsException("Invalid offset or length");
		}
		ensureCapacity(length);
		byteBuffer.put(bytes, offset, length);
	}

	@Override
	public synchronized void write(byte[] bytes) throws IOException {
		if (bytes == null) {
			throw new NullPointerException("Byte array cannot be null");
		}
		write(bytes, 0, bytes.length);
	}

	/**
	 * 确保有足够的容量写入数据
	 */
	protected void ensureCapacity(int required) {
		while (byteBuffer.remaining() < required) {
			int newCapacity = bufferSizeStrategy.nextSize(byteBuffer.capacity(), required);
			ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
			byteBuffer.flip();
			newBuffer.put(byteBuffer);
			byteBuffer = newBuffer;
		}
	}

	/**
	 * 获取 {@link ByteBuffer} 的副本，并切换到读模式
	 */
	@SneakyThrows
	public synchronized ByteBuffer getByteBuffer() {
		checkClosed();
		ByteBuffer duplicate = byteBuffer.duplicate();
		// 切换到读模式
		duplicate.flip();
		// 返回只读缓冲区防止外部修改
		return duplicate.asReadOnlyBuffer();
	}

	/**
	 * 转换为字节数组
	 */
	@SneakyThrows
	public byte[] toByteArray() {
		ByteBuffer byteBuffer = this.getByteBuffer();
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return bytes;
	}

	/**
	 * 获取当前写入的字节数
	 */
	public int size() {
		return byteBuffer.position();
	}

	/**
	 * 获取当前容量
	 */
	public int capacity() {
		return byteBuffer.capacity();
	}

	/**
	 * 重置输出流，清空所有数据
	 */
	@SneakyThrows
	public synchronized void reset() {
		checkClosed();
		byteBuffer.clear();
	}

	/**
	 * 关闭输出流
	 */
	@Override
	public void close() {
		closed = true;
	}

	protected void checkClosed() throws IOException {
		if (isClosed()) {
			throw new IOException(this.getClass() + " is closed");
		}
	}

	/**
	 * 缓冲区大小策略接口
	 */
	@FunctionalInterface
	public interface BufferSizeStrategy {
		/**
		 * 计算下一个缓冲区大小
		 *
		 * @param oldSize      上次的缓冲区大小
		 * @param requiredSize 需要写入的数据大小
		 * @return 新的缓冲区大小
		 */
		int nextSize(int oldSize, int requiredSize);
	}

	@RequiredArgsConstructor
	public static class DefaultBufferSizeStrategy implements BufferSizeStrategy {
		public static final DefaultBufferSizeStrategy DEFAULT = new DefaultBufferSizeStrategy(ArrayUtil.SAFE_MAX_ARRAY_SIZE);

		protected final int maxBufferSize;

		@Override
		public int nextSize(int oldSize, int requiredSize) {
			if (requiredSize > maxBufferSize || oldSize >= maxBufferSize / 2 || oldSize >= maxBufferSize - requiredSize) {
				return maxBufferSize;
			}
			return Math.max(oldSize * 2, oldSize + requiredSize);
		}
	}
}
