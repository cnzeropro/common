package org.zero.common.core.extension.java.io;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.zero.common.core.util.java.io.IoUtil.DEFAULT_BUFFER_SIZE;
import static org.zero.common.core.util.java.io.IoUtil.MAX_BUFFER_SIZE;
import static org.zero.common.core.util.java.lang.ArrayUtil.SAFE_MAX_ARRAY_SIZE;

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
	protected int currentBufferIndex = 0;

	/**
	 * 当前缓冲区中的写入位置
	 */
	protected int currentBufferPosition = 0;

	/**
	 * 总的写入字节数
	 */
	protected final AtomicReference<BigInteger> totalBytesWritten = new AtomicReference<>(BigInteger.ZERO);

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

	/**
	 * 是否关闭
	 */
	protected volatile boolean closed = false;

	public DynamicByteArrayOutputStream() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public DynamicByteArrayOutputStream(int initialBufferSize) {
		this(initialBufferSize, new DefaultBufferSizeStrategy(MAX_BUFFER_SIZE));
	}

	public DynamicByteArrayOutputStream(int initialBufferSize, BufferSizeStrategy strategy) {
		this.bufferSizeStrategy = strategy;
		// 预先分配第一个缓冲区
		this.allocateNewBuffer(initialBufferSize);
	}

	@Override
	public void write(int b) throws IOException {
		checkClosed();
		writeLock.lock();
		try {
			ensureCapacity(1);
			byte[] currentBuffer = buffers.get(currentBufferIndex);
			currentBuffer[currentBufferPosition++] = (byte) b;
			totalBytesWritten.updateAndGet(totalBytes -> totalBytes.add(BigInteger.ONE));
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		checkClosed();
		if (b == null) {
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
			int remaining = len;
			int sourceOffset = off;

			while (remaining > 0) {
				ensureCapacity(remaining);
				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int available = currentBuffer.length - currentBufferPosition;
				int toCopy = Math.min(remaining, available);

				System.arraycopy(b, sourceOffset, currentBuffer, currentBufferPosition, toCopy);

				currentBufferPosition += toCopy;
				sourceOffset += toCopy;
				remaining -= toCopy;
				totalBytesWritten.updateAndGet(totalBytes -> totalBytes.add(BigInteger.valueOf(toCopy)));

				// 如果当前缓冲区已满，准备下一个缓冲区
				if (currentBufferPosition == currentBuffer.length) {
					moveToNextBuffer();
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void close()  {
		closed = true;
	}

	/**
	 * 是否已关闭
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * 检查流是否已关闭
	 */
	protected void checkClosed() throws IOException {
		if (isClosed()) {
			throw new IOException("Stream closed");
		}
	}

	/**
	 * 确保有足够容量写入指定长度的数据
	 */
	protected void ensureCapacity(int required) {
		byte[] currentBuffer = buffers.get(currentBufferIndex);
		if (currentBufferPosition + required <= currentBuffer.length) {
			return;
		}

		// 当前缓冲区不足，分配新缓冲区
		int newBufferSize = bufferSizeStrategy.nextBufferSize(
			buffers.size(),
			currentBuffer.length,
			totalBytesWritten.get()
		);
		allocateNewBuffer(newBufferSize);
	}

	/**
	 * 分配新缓冲区
	 */
	protected void allocateNewBuffer(int size) {
		byte[] newBuffer = new byte[size];
		buffers.add(newBuffer);
		currentBufferIndex = buffers.size() - 1;
		currentBufferPosition = 0;
	}

	/**
	 * 移动到下一个缓冲区（当前缓冲区已满时）
	 */
	protected void moveToNextBuffer() {
		currentBufferIndex++;
		currentBufferPosition = 0;

		// 如果还没有对应的缓冲区，分配一个
		if (currentBufferIndex >= buffers.size()) {
			int newSize = bufferSizeStrategy.nextBufferSize(
				buffers.size(),
				buffers.get(buffers.size() - 1).length,
				totalBytesWritten.get()
			);
			allocateNewBuffer(newSize);
		}
	}

	/**
	 * 获取已写入的总字节数
	 */
	public BigInteger size() {
		return totalBytesWritten.get();
	}

	/**
	 * 重置流 - 清空所有数据
	 */
	@SneakyThrows
	public void reset() {
		checkClosed();
		writeLock.lock();
		try {
			buffers.clear();
			allocateNewBuffer(DEFAULT_BUFFER_SIZE);
			totalBytesWritten.set(BigInteger.ZERO);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 将内容写入到另一个输出流（线程安全）
	 */
	public void writeTo(OutputStream out) throws IOException {
		checkClosed();
		readLock.lock();
		try {
			BigInteger remaining = totalBytesWritten.get();
			for (int i = 0; i < buffers.size() && remaining.intValue() > 0; i++) {
				byte[] buffer = buffers.get(i);
				int toWrite = Math.min(buffer.length, remaining.intValue());
				out.write(buffer, 0, toWrite);
				remaining = remaining.subtract(BigInteger.valueOf(toWrite));
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 转换为字节数组（注意：海量数据时慎用）
	 */
	public byte[] toByteArray() {
		readLock.lock();
		try {
			BigInteger size = this.size();
			if (size.compareTo(BigInteger.valueOf(SAFE_MAX_ARRAY_SIZE)) > 0) {
				throw new IllegalStateException("Cannot convert to byte array: " + size + " too large");
			}
			int totalSize = size.intValue();
			byte[] result = new byte[totalSize];
			int offset = 0;
			int remaining = totalSize;

			for (int i = 0; i < buffers.size() && remaining > 0; i++) {
				byte[] buffer = buffers.get(i);
				int toCopy = Math.min(buffer.length, remaining);
				System.arraycopy(buffer, 0, result, offset, toCopy);
				offset += toCopy;
				remaining -= toCopy;
			}

			return result;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取缓冲区列表的副本
	 */
	public List<byte[]> getBuffersSnapshot() {
		readLock.lock();
		try {
			List<byte[]> snapshot = new ArrayList<>(buffers.size());
			for (byte[] buffer : buffers) {
				snapshot.add(Arrays.copyOf(buffer, buffer.length));
			}
			return snapshot;
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
	 * 缓冲区大小策略接口
	 */
	public interface BufferSizeStrategy {
		int nextBufferSize(int currentBufferCount, int lastBufferSize, BigInteger totalBytesWritten);
	}

	/**
	 * 默认缓冲区大小策略 - 指数增长直到最大值
	 */
	public static class DefaultBufferSizeStrategy implements BufferSizeStrategy {
		protected final int maxBufferSize;

		public DefaultBufferSizeStrategy(int maxBufferSize) {
			this.maxBufferSize = maxBufferSize;
		}

		@Override
		public int nextBufferSize(int currentBufferCount, int lastBufferSize, BigInteger totalBytesWritten) {
			if (currentBufferCount == 0) {
				return DEFAULT_BUFFER_SIZE;
			}
			// 指数增长，但不超过最大值
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
