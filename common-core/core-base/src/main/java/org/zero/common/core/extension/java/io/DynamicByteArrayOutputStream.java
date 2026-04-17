package org.zero.common.core.extension.java.io;

import lombok.Getter;
import lombok.SneakyThrows;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.zero.common.core.util.java.io.IoUtil.DEFAULT_BUFFER_SIZE;
import static org.zero.common.core.util.java.io.IoUtil.MAX_BUFFER_SIZE;

/**
 * 支持海量数据的字节数组输出流
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class DynamicByteArrayOutputStream extends OutputStream {
	/**
	 * 缓冲区列表
	 */
	protected final List<byte[]> buffers = new LinkedList<>();
	/**
	 * 当前写入的缓冲区索引
	 */
	protected int currentBufferIndex = -1;
	/**
	 * 当前缓冲区中的写入位置
	 */
	protected int currentBufferPosition = -1;
	/**
	 * 写入总字节数
	 */
	protected BigInteger totalBytesWritten = BigInteger.ZERO;

	/**
	 * 读写锁
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	/**
	 * 缓冲区大小策略
	 */
	protected final BufferSizeStrategy bufferSizeStrategy;
	protected final int initialBufferSize;

	/**
	 * 是否关闭
	 */
	@Getter
	protected volatile boolean closed = false;

	public DynamicByteArrayOutputStream() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public DynamicByteArrayOutputStream(int initialBufferSize) {
		this(initialBufferSize, new DefaultBufferSizeStrategy());
	}

	public DynamicByteArrayOutputStream(int initialBufferSize, BufferSizeStrategy strategy) {
		// 预先分配第一个缓冲区
		this.initialBufferSize = validateInitialBufferSize(initialBufferSize);
		this.bufferSizeStrategy = Objects.requireNonNull(strategy, "Buffer size strategy cannot be null");
		this.allocateNewBuffer(this.initialBufferSize);
	}

	@Override
	public void write(int b) throws IOException {
		this.checkClosed();
		writeLock.lock();
		try {
			// 如果当前缓冲区已满，准备下一个缓冲区
			if (this.isCurrentBufferFull()) {
				this.allocateNextBuffer();
			}
			byte[] currentBuffer = buffers.get(currentBufferIndex);
			currentBuffer[currentBufferPosition++] = (byte) b;
			totalBytesWritten = totalBytesWritten.add(BigInteger.ONE);
		} finally {
			writeLock.unlock();
		}
	}

	protected static int validateInitialBufferSize(int initialBufferSize) {
		if (initialBufferSize <= 0) {
			throw new IllegalArgumentException("Initial buffer size must be positive");
		}
		return initialBufferSize;
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		checkClosed();
		if (Objects.isNull(b)) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return;
		}

		writeLock.lock();
		try {
			while (len > 0) {
				// 如果当前缓冲区已满，准备下一个缓冲区
				if (this.isCurrentBufferFull()) {
					this.allocateNextBuffer();
				}

				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int available = currentBuffer.length - currentBufferPosition;
				int toCopy = Math.min(len, available);
				System.arraycopy(b, off, currentBuffer, currentBufferPosition, toCopy);

				currentBufferPosition += toCopy;
				off += toCopy;
				len -= toCopy;
				totalBytesWritten = totalBytesWritten.add(BigInteger.valueOf(toCopy));
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 获取已写入的总字节数
	 */
	public BigInteger size() {
		readLock.lock();
		try {
			return totalBytesWritten;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 将内容写入到另一个输出流
	 */
	@SneakyThrows
	public void writeTo(OutputStream out) {
		checkClosed();
		readLock.lock();
		try {
			BigInteger remaining = totalBytesWritten;
			for (int i = 0; i < buffers.size() && remaining.compareTo(BigInteger.ZERO) > 0; i++) {
				byte[] buffer = buffers.get(i);
				int maxWriteable = remaining.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0 ? Integer.MAX_VALUE : remaining.intValue();
				int toWrite = Math.min(buffer.length, maxWriteable);
				out.write(buffer, 0, toWrite);
				remaining = remaining.subtract(BigInteger.valueOf(toWrite));
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 转换为 {@link ChunkedByteBuffer}
	 */
	@SneakyThrows
	public ChunkedByteBuffer toChunkedByteBuffer() {
		return this.toChunkedByteBuffer(ArrayUtil.SAFE_MAX_ARRAY_SIZE);
	}

	/**
	 * 转换为 {@link ChunkedByteBuffer}
	 */
	@SneakyThrows
	public ChunkedByteBuffer toChunkedByteBuffer(int maxBlockSize) {
		checkClosed();
		readLock.lock();
		try {
			ChunkedByteBuffer chunkedByteBuffer = new ChunkedByteBuffer(maxBlockSize);
			BigInteger remaining = totalBytesWritten;
			for (int i = 0; i < buffers.size() && remaining.compareTo(BigInteger.ZERO) > 0; i++) {
				byte[] buffer = buffers.get(i);
				int maxWriteable = remaining.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0 ? Integer.MAX_VALUE : remaining.intValue();
				int toWrite = Math.min(buffer.length, maxWriteable);
				chunkedByteBuffer.put(buffer, 0, toWrite);
				remaining = remaining.subtract(BigInteger.valueOf(toWrite));
			}
			return chunkedByteBuffer;
		} finally {
			readLock.unlock();
		}
	}


	/**
	 * 获取当前缓冲区数量
	 */
	public int getBufferCount() {
		readLock.lock();
		try {
			return buffers.size();
		} finally {
			readLock.unlock();
		}
	}


	/**
	 * 检查流是否已关闭
	 */
	protected void checkClosed() throws IOException {
		if (isClosed()) {
			throw new IOException(this.getClass() + " is closed");
		}
	}

	/**
	 * 判断当前缓冲区是否已满
	 */
	protected boolean isCurrentBufferFull() {
		byte[] currentBuffer = buffers.get(currentBufferIndex);
		return currentBufferPosition >= currentBuffer.length;
	}

	/**
	 * 重置流
	 * <p>
	 * 清空所有数据
	 */
	@SneakyThrows
	public void reset() {
		checkClosed();
		writeLock.lock();
		try {
			buffers.clear();
			totalBytesWritten = BigInteger.ZERO;
			currentBufferIndex = -1;
			currentBufferPosition = -1;
			this.allocateNewBuffer(initialBufferSize);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 分配下一个缓冲区
	 */
	protected void allocateNextBuffer() {
		byte[] currentBuffer = buffers.get(currentBufferIndex);
		int newBufferSize = bufferSizeStrategy.nextBufferSize(
			buffers.size(),
			currentBuffer.length,
			totalBytesWritten
		);
		if (newBufferSize <= 0) {
			throw new IllegalStateException("Buffer size strategy must return a positive buffer size");
		}
		this.allocateNewBuffer(newBufferSize);
	}

	/**
	 * 分配新的缓冲区
	 */
	protected void allocateNewBuffer(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Buffer size must be positive");
		}
		byte[] newBuffer = new byte[size];
		buffers.add(newBuffer);
		currentBufferIndex++;
		currentBufferPosition = 0;
	}

	/**
	 * 缓冲区大小策略接口
	 */
	public interface BufferSizeStrategy {
		int nextBufferSize(int currentBufferCount, int lastBufferSize, BigInteger totalBytesWritten);
	}

	/**
	 * 默认缓冲区大小策略
	 * <p>
	 * 指数增长直到最大值
	 */
	public static class DefaultBufferSizeStrategy implements BufferSizeStrategy {
		protected final int maxBufferSize;

		public DefaultBufferSizeStrategy() {
			this(MAX_BUFFER_SIZE);
		}

		public DefaultBufferSizeStrategy(int maxBufferSize) {
			this.maxBufferSize = maxBufferSize;
		}

		@Override
		public int nextBufferSize(int currentBufferCount, int lastBufferSize, BigInteger totalBytesWritten) {
			if (lastBufferSize >= maxBufferSize / 2) {
				return maxBufferSize;
			}
			int newSize = lastBufferSize * 2;
			return Math.min(newSize, maxBufferSize);
		}
	}

	/**
	 * 固定大小缓冲区策略
	 */
	public static class FixedBufferSizeStrategy implements BufferSizeStrategy {
		protected final int bufferSize;

		public FixedBufferSizeStrategy(int bufferSize) {
			this.bufferSize = bufferSize;
		}

		@Override
		public int nextBufferSize(int currentBufferCount, int lastBufferSize, BigInteger totalBytesWritten) {
			return bufferSize;
		}
	}
}
