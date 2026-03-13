package org.zero.common.core.extension.java.io;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.NumberUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/5
 */
@RequiredArgsConstructor
public class HugeInputStream extends InputStream {
	/**
	 * 默认缓冲区大小，64M
	 */
	protected final int DEFAULT_BUFFER_SIZE = 1 << 26;
	protected final InputStream delegate;

	/**
	 * 标记位置
	 */
	protected BigInteger markPosition = BigInteger.ONE.negate();

	@Override
	public int read() throws IOException {
		return delegate.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return delegate.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return delegate.read(b, off, len);
	}

	/**
	 * 读取指定长度数据到 {@link ChunkedByteBuffer}
	 *
	 * @param chunkedByteBuffer 缓冲区
	 * @param length            长度
	 * @return 实际读取的字节数
	 */
	public BigInteger read(ChunkedByteBuffer chunkedByteBuffer, BigInteger length) throws IOException {
		return chunkedByteBuffer.readFrom(this, BigInteger.ZERO, length, false);
	}

	@Override
	public long skip(long n) throws IOException {
		return delegate.skip(n);
	}

	public BigInteger skip(BigInteger skipBytes) throws IOException {
		int bytesRead;
		byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
		BigInteger totalSkipped = BigInteger.ZERO;
		// 跳过字节，不使用 skip 相关方法来跳过，在大数据流或者其他网络流时可能遇到问题
		if (Objects.nonNull(skipBytes) && skipBytes.compareTo(BigInteger.ZERO) > 0) {
			while (totalSkipped.compareTo(skipBytes) < 0 &&
				(bytesRead = delegate.read(bytes, 0, Math.min(bytes.length, NumberUtil.toInt(skipBytes.subtract(totalSkipped))))) != -1) {
				totalSkipped = totalSkipped.add(BigInteger.valueOf(bytesRead));
			}
		}
		return totalSkipped;
	}

	@Override
	public int available() throws IOException {
		return delegate.available();
	}

	@Override
	public boolean markSupported() {
		return delegate.markSupported();
	}

	@Override
	public void reset() throws IOException {
		delegate.reset();
	}

	@Override
	public void mark(int readlimit) {
		delegate.mark(readlimit);
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}
}
