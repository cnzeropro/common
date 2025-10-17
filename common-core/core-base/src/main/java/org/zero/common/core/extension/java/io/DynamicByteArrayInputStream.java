package org.zero.common.core.extension.java.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 支持海量数据的字节数组输入流
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/16
 */

public class DynamicByteArrayInputStream extends InputStream {
	/**
	 * 缓冲区列表
	 */
	protected final List<byte[]> buffers;
	/**
	 * 总数据大小
	 */
	protected final BigInteger totalSize;

	/**
	 * 当前读取位置（总字节偏移）
	 */
	protected final AtomicReference<BigInteger> currentPosition = new AtomicReference<>(BigInteger.ZERO);
	/**
	 * 当前缓冲区索引
	 */
	protected int currentBufferIndex = 0;
	/**
	 * 当前缓冲区偏移
	 */
	protected int currentBufferOffset = 0;

	// 标记支持
	protected BigInteger markPosition = BigInteger.valueOf(-1);
	protected int markBufferIndex = 0;
	protected int markBufferOffset = 0;

	/**
	 * 读写锁 - 支持并发读取
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

	/**
	 * 是否关闭
	 */
	protected volatile boolean closed = false;

	public DynamicByteArrayInputStream(DynamicByteArrayOutputStream dynamicByteArrayOutputStream) {
		this(dynamicByteArrayOutputStream.getBuffersSnapshot(), dynamicByteArrayOutputStream.size());
	}

	/**
	 * 从缓冲区列表和总大小构建输入流
	 */
	public DynamicByteArrayInputStream(List<byte[]> buffers, BigInteger totalSize) {
		if (buffers == null) {
			throw new NullPointerException("buffers cannot be null");
		}
		if (totalSize.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("totalSize cannot be negative");
		}

		this.buffers = buffers;
		this.totalSize = totalSize;

		// 验证缓冲区总容量至少等于totalSize
		validateBuffers(buffers, totalSize);
	}

	/**
	 * 验证缓冲区容量
	 */
	protected void validateBuffers(List<byte[]> buffers, BigInteger totalSize) {
		BigInteger totalCapacity = BigInteger.ZERO;
		for (byte[] buffer : buffers) {
			if (buffer == null) {
				throw new IllegalArgumentException("buffer in list cannot be null");
			}
			totalCapacity = totalCapacity.add(BigInteger.valueOf(buffer.length));
		}

		if (totalCapacity.compareTo(totalSize) < 0) {
			throw new IllegalArgumentException("Total buffer capacity " + totalCapacity +
				" is less than totalSize " + totalSize);
		}
	}

	@Override
	public int read() throws IOException {
		checkClosed();

		readLock.lock();
		try {
			if (totalSize.compareTo(currentPosition.get()) < 0) {
				return -1;
			}
			// 获取当前缓冲区
			byte[] currentBuffer = buffers.get(currentBufferIndex);
			int result = currentBuffer[currentBufferOffset++] & 0xFF;

			// 如果当前缓冲区已读完，移动到下一个缓冲区
			if (currentBufferOffset >= currentBuffer.length) {
				moveToNextBuffer();
			}
			currentPosition.updateAndGet(totalBytes -> totalBytes.add(BigInteger.ONE));
			return result;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkClosed();

		if (b == null) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}

		readLock.lock();
		try {
			BigInteger remaining = totalSize.subtract(currentPosition.get());
			if (remaining.compareTo(BigInteger.ZERO) <= 0) {
				return -1;
			}

			int bytesToRead = Math.min(len, remaining.intValue());
			int bytesRead = 0;
			int destOffset = off;

			while (bytesRead < bytesToRead) {
				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int availableInBuffer = currentBuffer.length - currentBufferOffset;

				// 如果当前缓冲区没有更多数据，但总数据还有，说明这是最后一个缓冲区
				if (availableInBuffer <= 0) {
					if (!moveToNextBuffer()) {
						break; // 没有更多缓冲区了
					}
					currentBuffer = buffers.get(currentBufferIndex);
					availableInBuffer = currentBuffer.length - currentBufferOffset;
				}

				int toRead = Math.min(bytesToRead - bytesRead, availableInBuffer);
				System.arraycopy(currentBuffer, currentBufferOffset, b, destOffset, toRead);

				currentBufferOffset += toRead;
				destOffset += toRead;
				bytesRead += toRead;

				// 如果当前缓冲区读完，移动到下一个
				if (currentBufferOffset >= currentBuffer.length) {
					if (!moveToNextBuffer()) {
						break;
					}
				}
			}

			BigInteger value = BigInteger.valueOf(bytesRead);
			currentPosition.updateAndGet(totalBytes -> totalBytes.add(value));
			return bytesRead;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 移动到下一个缓冲区
	 *
	 * @return 是否成功移动到下一个缓冲区
	 */
	protected boolean moveToNextBuffer() {
		if (currentBufferIndex + 1 < buffers.size()) {
			currentBufferIndex++;
			currentBufferOffset = 0;
			return true;
		}
		return false;
	}

	@Override
	public int available() throws IOException {
		checkClosed();
		return totalSize.subtract(currentPosition.get()).intValue();
	}

	@Override
	public long skip(long n) throws IOException {
		checkClosed();

		if (n <= 0) {
			return 0;
		}

		readLock.lock();
		try {
			long remaining = totalSize.subtract(currentPosition.get()).longValue();
			long bytesToSkip = Math.min(n, remaining);

			if (bytesToSkip == 0) {
				return 0;
			}

			// 计算跳过的字节数并更新位置
			int skipped = 0;
			while (skipped < bytesToSkip) {
				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int availableInBuffer = currentBuffer.length - currentBufferOffset;
				int toSkip = (int) Math.min(bytesToSkip - skipped, availableInBuffer);

				currentBufferOffset += toSkip;
				skipped += toSkip;

				// 如果当前缓冲区跳过完毕，移动到下一个
				if (currentBufferOffset >= currentBuffer.length) {
					if (!moveToNextBuffer()) {
						break;
					}
				}
			}

			BigInteger value = BigInteger.valueOf(skipped);
			currentPosition.updateAndGet(totalBytes -> totalBytes.add(value));
			return skipped;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override

	public synchronized void mark(int readlimit) {
		markPosition = currentPosition.get();
		markBufferIndex = currentBufferIndex;
		markBufferOffset = currentBufferOffset;
	}

	@Override
	public synchronized void reset() throws IOException {
		checkClosed();

		if (markPosition.compareTo(BigInteger.ZERO) < 0) {
			throw new IOException("Mark not set");
		}

		readLock.lock();
		try {
			currentPosition.set(markPosition);
			currentBufferIndex = markBufferIndex;
			currentBufferOffset = markBufferOffset;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取当前读取位置
	 */
	public BigInteger getPosition() {
		return currentPosition.get();
	}

	/**
	 * 获取总数据大小
	 */
	public BigInteger getTotalSize() {
		return totalSize;
	}

	/**
	 * 获取缓冲区数量
	 */
	public int getBufferCount() {
		return buffers.size();
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

	@Override
	public void close() throws IOException {
		closed = true;
	}

	/**
	 * 创建一个从指定位置开始的新的输入流
	 * <p>
	 * 注意：这会创建一个新的流实例，与原流独立
	 */
	public DynamicByteArrayInputStream newStreamFromPosition(BigInteger position) throws IOException {
		checkClosed();

		if (position.compareTo(BigInteger.ZERO) < 0 || position.compareTo(totalSize) > 0) {
			throw new IllegalArgumentException("Position out of range: " + position);
		}

		// 创建新的流实例
		DynamicByteArrayInputStream newStream = new DynamicByteArrayInputStream(buffers, totalSize);
		// 跳转到指定位置
		BigInteger needSkip = position;
		do {
			if (needSkip.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				newStream.skip(Long.MAX_VALUE);
				needSkip = needSkip.subtract(BigInteger.valueOf(Long.MAX_VALUE));
			} else {
				newStream.skip(needSkip.longValue());
				needSkip = BigInteger.ZERO;
			}
		} while (needSkip.compareTo(BigInteger.ZERO) > 0);
		return newStream;
	}
}
