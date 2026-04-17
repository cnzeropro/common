package org.zero.common.core.extension.java.io;

import lombok.Getter;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.NumberUtil;
import org.zero.common.core.util.java.util.CollectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	 * 当前读取位置
	 */
	protected BigInteger currentPosition = BigInteger.ZERO;
	/**
	 * 当前缓冲区索引
	 */
	protected int currentBufferIndex = 0;
	/**
	 * 当前缓冲区偏移
	 */
	protected int currentBufferOffset = 0;

	/**
	 * 标记位置
	 */
	protected BigInteger markPosition = BigInteger.ONE.negate();
	/**
	 * 标记缓冲区索引
	 */
	protected int markBufferIndex = 0;
	/**
	 * 标记缓冲区偏移
	 */
	protected int markBufferOffset = 0;

	/**
	 * 读写锁
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	/**
	 * 是否关闭
	 */
	@Getter
	protected volatile boolean closed = false;

	public DynamicByteArrayInputStream(ChunkedByteBuffer chunkedByteBuffer) {
		this(chunkedByteBuffer.blocks, chunkedByteBuffer.size());
	}

	public DynamicByteArrayInputStream(DynamicByteArrayOutputStream dynamicByteArrayOutputStream) {
		this(dynamicByteArrayOutputStream.buffers, dynamicByteArrayOutputStream.size());
	}

	public DynamicByteArrayInputStream(Collection<byte[]> buffers) {
		this.buffers = new LinkedList<>();
		BigInteger totalSize = BigInteger.ZERO;
		if (CollectionUtil.nonEmpty(buffers)) {
			for (byte[] buffer : buffers) {
				if (ArrayUtil.isEmpty(buffer)) {
					continue;
				}
				this.buffers.add(buffer);
				totalSize = totalSize.add(BigInteger.valueOf(buffer.length));
			}
		}
		this.totalSize = totalSize;
	}

	public DynamicByteArrayInputStream(byte[]... buffers) {
		this(ArrayUtil.isEmpty(buffers) ? Collections.emptyList() : Arrays.asList(buffers));
	}

	public DynamicByteArrayInputStream(Map<byte[], Integer> buffers) {
		this.buffers = new LinkedList<>();
		BigInteger totalSize = BigInteger.ZERO;
		for (Map.Entry<byte[], Integer> entry : buffers.entrySet()) {
			byte[] buffer = entry.getKey();
			Integer size = entry.getValue();
			if (Objects.isNull(buffer) || Objects.isNull(size) || size <= 0) {
				continue;
			}
			int bufferLength = buffer.length;
			if (size >= bufferLength) {
				this.buffers.add(buffer);
				totalSize = totalSize.add(BigInteger.valueOf(bufferLength));
				continue;
			}
			byte[] bytes = new byte[size];
			System.arraycopy(buffer, 0, bytes, 0, size);
			this.buffers.add(bytes);
			totalSize = totalSize.add(BigInteger.valueOf(size));
		}
		this.totalSize = totalSize;
	}

	protected DynamicByteArrayInputStream(List<byte[]> buffers, BigInteger totalSize) {
		this.buffers = new LinkedList<>(buffers);
		this.totalSize = totalSize;
	}

	@Override
	public int read() throws IOException {
		this.checkClosed();

		writeLock.lock();
		try {
			if (totalSize.compareTo(currentPosition) <= 0) {
				return -1;
			}
			if (this.isCurrentBufferExhausted() && !this.moveToNextBuffer()) {
				return -1;
			}
			byte[] currentBuffer = buffers.get(currentBufferIndex);
			int result = currentBuffer[currentBufferOffset++] & 0xFF;
			currentPosition = currentPosition.add(BigInteger.ONE);
			return result;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		this.checkClosed();

		if (Objects.isNull(b)) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}

		writeLock.lock();
		try {
			BigInteger remaining = totalSize.subtract(currentPosition);
			if (remaining.compareTo(BigInteger.ZERO) <= 0) {
				return -1;
			}

			int maxReadable = remaining.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0 ? Integer.MAX_VALUE : remaining.intValue();
			int bytesToRead = Math.min(len, maxReadable);
			int bytesRead = 0;
			int offset = off;

			while (bytesRead < bytesToRead) {
				if (this.isCurrentBufferExhausted() && !this.moveToNextBuffer()) {
					break;
				}
				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int availableInBuffer = currentBuffer.length - currentBufferOffset;
				int toRead = Math.min(bytesToRead - bytesRead, availableInBuffer);
				System.arraycopy(currentBuffer, currentBufferOffset, b, offset, toRead);

				currentBufferOffset += toRead;
				offset += toRead;
				bytesRead += toRead;
			}

			currentPosition = currentPosition.add(BigInteger.valueOf(bytesRead));
			return bytesRead;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int available() throws IOException {
		this.checkClosed();
		readLock.lock();
		try {
			return NumberUtil.toInt(totalSize.subtract(currentPosition));
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public long skip(long n) throws IOException {
		this.checkClosed();

		if (n <= 0) {
			return 0;
		}

		writeLock.lock();
		try {
			BigInteger remaining = totalSize.subtract(currentPosition);
			if (remaining.compareTo(BigInteger.ZERO) <= 0) {
				return 0;
			}
			long maxSkip = NumberUtil.toLong(remaining);
			long bytesToSkip = Math.min(n, maxSkip);
			if (bytesToSkip == 0) {
				return 0;
			}

			// 计算跳过的字节数并更新位置
			long skipped = 0;
			while (skipped < bytesToSkip) {
				if (this.isCurrentBufferExhausted() && !this.moveToNextBuffer()) {
					break;
				}
				byte[] currentBuffer = buffers.get(currentBufferIndex);
				int availableInBuffer = currentBuffer.length - currentBufferOffset;
				int toSkip = (int) Math.min(bytesToSkip - skipped, availableInBuffer);
				currentBufferOffset += toSkip;
				skipped += toSkip;
			}
			currentPosition = currentPosition.add(BigInteger.valueOf(skipped));
			return skipped;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readlimit) {
		if (isClosed()) {
			return;
		}
		writeLock.lock();
		try {
			if (isClosed()) {
				return;
			}
			markPosition = currentPosition;
			markBufferIndex = currentBufferIndex;
			markBufferOffset = currentBufferOffset;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void reset() throws IOException {
		this.checkClosed();

		if (markPosition.compareTo(BigInteger.ZERO) < 0) {
			throw new IOException("Mark not set");
		}

		writeLock.lock();
		try {
			currentPosition = markPosition;
			currentBufferIndex = markBufferIndex;
			currentBufferOffset = markBufferOffset;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void close() {
		closed = true;
	}

	public BigInteger remaining() {
		readLock.lock();
		try {
			return totalSize.subtract(currentPosition);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取当前读取位置
	 */
	public BigInteger getPosition() {
		readLock.lock();
		try {
			return currentPosition;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取总数据大小
	 */
	public BigInteger getSize() {
		readLock.lock();
		try {
			return totalSize;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取缓冲区数量
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
	 * 判断当前缓冲区是否已读完
	 *
	 * @return 当前缓冲区是否已读完
	 */
	protected boolean isCurrentBufferExhausted() {
		if (currentBufferIndex >= buffers.size()) {
			return true;
		}
		byte[] currentBuffer = buffers.get(currentBufferIndex);
		return currentBufferOffset >= currentBuffer.length;
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

	/**
	 * 检查流是否已关闭
	 */
	protected void checkClosed() throws IOException {
		if (isClosed()) {
			throw new IOException(this.getClass() + " is closed");
		}
	}
}
