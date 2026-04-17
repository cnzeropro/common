package org.zero.common.core.extension.java.io;

import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.NumberUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 分块字节缓冲区。
 * <p>
 * 如果存放数据小于 2GiB，建议优先使用 {@link java.nio.ByteBuffer}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
public class ChunkedByteBuffer implements Serializable {
	/**
	 * 数据块。
	 */
	protected final List<byte[]> blocks = new LinkedList<>();
	/**
	 * 每块最大容量。
	 */
	protected final int maxBlockCapacity;

	public ChunkedByteBuffer() {
		this(ArrayUtil.SAFE_MAX_ARRAY_SIZE);
	}

	public ChunkedByteBuffer(int maxBlockCapacity) {
		this.maxBlockCapacity = validateMaxBlockCapacity(maxBlockCapacity);
	}

	/**
	 * 添加单个字节。
	 *
	 * @param b 数据
	 */
	public synchronized void put(byte b) {
		if (blocks.isEmpty()) {
			byte[] block = new byte[1];
			block[0] = b;
			blocks.add(block);
			return;
		}
		int lastBlockIndex = blocks.size() - 1;
		byte[] lastBlock = blocks.get(lastBlockIndex);
		int lastBlockLength = lastBlock.length;
		if (lastBlockLength >= maxBlockCapacity) {
			byte[] block = new byte[1];
			block[0] = b;
			blocks.add(block);
			return;
		}
		byte[] block = new byte[lastBlockLength + 1];
		System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
		block[lastBlockLength] = b;
		blocks.set(lastBlockIndex, block);
	}

	/**
	 * 添加数据。
	 *
	 * @param bytes 数据
	 */
	public void put(byte[] bytes) {
		Objects.requireNonNull(bytes, "Byte array cannot be null");
		this.put(bytes, 0, bytes.length);
	}

	/**
	 * 添加数据。
	 *
	 * @param bytes 数据
	 * @param offset 数据偏移量
	 */
	public void put(byte[] bytes, int offset) {
		Objects.requireNonNull(bytes, "Byte array cannot be null");
		this.put(bytes, offset, bytes.length - offset);
	}

	/**
	 * 添加数据。
	 *
	 * @param bytes 数据
	 * @param offset 数据偏移量
	 * @param length 数据长度
	 */
	public synchronized void put(byte[] bytes, int offset, int length) {
		if (Objects.isNull(bytes)) {
			throw new NullPointerException("Byte array cannot be null");
		}
		if (offset < 0 || length < 0 || length > bytes.length - offset) {
			throw new IndexOutOfBoundsException();
		}
		if (length == 0) {
			return;
		}

		if (blocks.isEmpty()) {
			if (length <= maxBlockCapacity) {
				byte[] block = new byte[length];
				System.arraycopy(bytes, offset, block, 0, length);
				blocks.add(block);
				return;
			}
			while (length > 0) {
				int blockLength = Math.min(maxBlockCapacity, length);
				byte[] block = new byte[blockLength];
				System.arraycopy(bytes, offset, block, 0, blockLength);
				blocks.add(block);
				offset += blockLength;
				length -= blockLength;
			}
			return;
		}

		int lastBlockIndex = blocks.size() - 1;
		byte[] lastBlock = blocks.get(lastBlockIndex);
		int lastBlockLength = lastBlock.length;
		int lastBlockRemaining = maxBlockCapacity - lastBlockLength;
		if (length <= lastBlockRemaining) {
			byte[] block = new byte[lastBlockLength + length];
			System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
			System.arraycopy(bytes, offset, block, lastBlockLength, length);
			blocks.set(lastBlockIndex, block);
			return;
		}
		if (lastBlockRemaining > 0) {
			byte[] block = new byte[maxBlockCapacity];
			System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
			System.arraycopy(bytes, offset, block, lastBlockLength, lastBlockRemaining);
			blocks.set(lastBlockIndex, block);
			offset += lastBlockRemaining;
			length -= lastBlockRemaining;
		}
		while (length > 0) {
			int blockLength = Math.min(maxBlockCapacity, length);
			byte[] block = new byte[blockLength];
			System.arraycopy(bytes, offset, block, 0, blockLength);
			blocks.add(block);
			offset += blockLength;
			length -= blockLength;
		}
	}

	/**
	 * 添加另一个缓冲区内容。
	 *
	 * @param buffer 数据
	 */
	public void put(ChunkedByteBuffer buffer) {
		if (Objects.isNull(buffer)) {
			return;
		}
		if (buffer == this) {
			synchronized (this) {
				this.appendBlocks(this.snapshotBlocks());
			}
			return;
		}
		List<byte[]> snapshot = buffer.snapshotBlocks();
		if (snapshot.isEmpty()) {
			return;
		}
		synchronized (this) {
			this.appendBlocks(snapshot);
		}
	}

	/**
	 * 获取数据。
	 *
	 * @param bytes 数据容器
	 * @return 写入的字节数
	 */
	public int get(byte[] bytes) {
		return this.get(BigInteger.ZERO, bytes);
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param bytes 数据容器
	 * @return 写入的字节数
	 */
	public int get(long offset, byte[] bytes) {
		return this.get(BigInteger.valueOf(offset), bytes);
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param bytes 数据容器
	 * @return 写入的字节数
	 */
	public synchronized int get(BigInteger offset, byte[] bytes) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(bytes)) {
			throw new IllegalArgumentException("Byte array cannot be null");
		}
		if (blocks.isEmpty() || bytes.length == 0) {
			return 0;
		}
		if (offset.compareTo(this.size()) >= 0) {
			return 0;
		}
		return this.get(offset, bytes, 0, bytes.length);
	}

	/**
	 * 获取数据。
	 *
	 * @param length 长度
	 * @return 数据
	 */
	public byte[] get(int length) {
		return this.get(BigInteger.ZERO, length);
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public byte[] get(long offset, int length) {
		return this.get(BigInteger.valueOf(offset), length);
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public synchronized byte[] get(BigInteger offset, int length) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (length < 0) {
			throw new IllegalArgumentException("Length must be non-negative");
		}
		if (blocks.isEmpty() || length == 0) {
			return new byte[0];
		}
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0) {
			return new byte[0];
		}
		BigInteger remaining = size.subtract(offset);
		if (BigInteger.valueOf(length).compareTo(remaining) > 0) {
			length = remaining.intValue();
		}
		byte[] result = new byte[length];
		this.get(offset, result, 0, length);
		return result;
	}

	protected int get(BigInteger offset, byte[] dest, int destOffset, int length) {
		if (length <= 0) {
			return 0;
		}
		ReadPosition readPosition = this.findReadPosition(offset);
		if (Objects.isNull(readPosition)) {
			return 0;
		}
		int bytesRead = 0;
		int resultCurrentOffset = destOffset;
		int currentBlockIndex = readPosition.blockIndex;
		int currentBlockOffset = readPosition.blockOffset;
		while (bytesRead < length && currentBlockIndex < blocks.size()) {
			byte[] currentBlock = blocks.get(currentBlockIndex);
			int availableInBlock = currentBlock.length - currentBlockOffset;
			int bytesToCopy = Math.min(availableInBlock, length - bytesRead);
			System.arraycopy(currentBlock, currentBlockOffset, dest, resultCurrentOffset, bytesToCopy);
			bytesRead += bytesToCopy;
			resultCurrentOffset += bytesToCopy;
			currentBlockIndex++;
			currentBlockOffset = 0;
		}
		return bytesRead;
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @return 数据
	 */
	public ChunkedByteBuffer get(long offset) {
		return this.get(BigInteger.valueOf(offset));
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @return 数据
	 */
	public ChunkedByteBuffer get(BigInteger offset) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		BigInteger size = this.size();
		if (size.compareTo(offset) <= 0) {
			return new ChunkedByteBuffer(maxBlockCapacity);
		}
		return this.get(offset, size.subtract(offset));
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public ChunkedByteBuffer get(long offset, BigInteger length) {
		return this.get(BigInteger.valueOf(offset), length);
	}

	/**
	 * 获取数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public synchronized ChunkedByteBuffer get(BigInteger offset, BigInteger length) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(length) || length.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Length must be non-negative");
		}
		ChunkedByteBuffer result = new ChunkedByteBuffer(maxBlockCapacity);
		if (blocks.isEmpty() || length.compareTo(BigInteger.ZERO) == 0) {
			return result;
		}
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0) {
			return result;
		}

		BigInteger remaining = size.subtract(offset);
		if (length.compareTo(remaining) > 0) {
			length = remaining;
		}

		ReadPosition readPosition = this.findReadPosition(offset);
		if (Objects.isNull(readPosition)) {
			return result;
		}

		int currentBlockIndex = readPosition.blockIndex;
		int currentBlockOffset = readPosition.blockOffset;
		while (length.compareTo(BigInteger.ZERO) > 0 && currentBlockIndex < blocks.size()) {
			byte[] currentBlock = blocks.get(currentBlockIndex);
			int availableInBlock = currentBlock.length - currentBlockOffset;
			int maxCopy = length.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0 ? Integer.MAX_VALUE : length.intValue();
			int bytesToCopy = Math.min(availableInBlock, maxCopy);
			byte[] chunk = new byte[bytesToCopy];
			System.arraycopy(currentBlock, currentBlockOffset, chunk, 0, bytesToCopy);
			result.put(chunk);
			length = length.subtract(BigInteger.valueOf(bytesToCopy));
			currentBlockIndex++;
			currentBlockOffset = 0;
		}
		return result;
	}

	/**
	 * 在指定位置插入数据。
	 *
	 * @param offset 偏移量
	 * @param bytes 插入数据
	 */
	public synchronized void insert(long offset, byte[] bytes) {
		this.insert(BigInteger.valueOf(offset), bytes);
	}

	/**
	 * 在指定位置插入数据。
	 *
	 * @param offset 偏移量
	 * @param bytes 插入数据
	 */
	public synchronized void insert(BigInteger offset, byte[] bytes) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(bytes) || bytes.length == 0) {
			return;
		}
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0) {
			this.put(bytes);
			return;
		}

		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		ChunkedByteBuffer secondPart = this.get(offset, size.subtract(offset));
		this.clear();
		this.appendBlocks(firstPart.blocks);
		this.put(bytes);
		this.appendBlocks(secondPart.blocks);
	}

	/**
	 * 在指定位置插入数据。
	 *
	 * @param offset 偏移量
	 * @param buffer 插入数据
	 */
	public void insert(long offset, ChunkedByteBuffer buffer) {
		this.insert(BigInteger.valueOf(offset), buffer);
	}

	/**
	 * 在指定位置插入数据。
	 *
	 * @param offset 偏移量
	 * @param buffer 插入数据
	 */
	public void insert(BigInteger offset, ChunkedByteBuffer buffer) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(buffer)) {
			return;
		}
		if (buffer == this) {
			synchronized (this) {
				List<byte[]> snapshot = this.snapshotBlocks();
				if (snapshot.isEmpty()) {
					return;
				}
				this.insertSnapshot(offset, snapshot);
			}
			return;
		}
		List<byte[]> snapshot = buffer.snapshotBlocks();
		if (snapshot.isEmpty()) {
			return;
		}
		synchronized (this) {
			this.insertSnapshot(offset, snapshot);
		}
	}

	/**
	 * 删除指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(BigInteger offset, long length) {
		this.delete(offset, BigInteger.valueOf(length));
	}

	/**
	 * 删除指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(long offset, BigInteger length) {
		this.delete(BigInteger.valueOf(offset), length);
	}

	/**
	 * 删除指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(long offset, long length) {
		this.delete(BigInteger.valueOf(offset), BigInteger.valueOf(length));
	}

	/**
	 * 删除指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public synchronized void delete(BigInteger offset, BigInteger length) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(length) || length.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Length must be non-negative");
		}
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0 || length.equals(BigInteger.ZERO)) {
			return;
		}

		BigInteger remaining = size.subtract(offset);
		if (length.compareTo(remaining) > 0) {
			length = remaining;
		}

		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		BigInteger secondPartOffset = offset.add(length);
		ChunkedByteBuffer secondPart = this.get(secondPartOffset, size.subtract(secondPartOffset));
		this.clear();
		this.appendBlocks(firstPart.blocks);
		this.appendBlocks(secondPart.blocks);
	}

	/**
	 * 替换指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param newData 新数据
	 */
	public void replace(long offset, byte[] newData) {
		this.replace(BigInteger.valueOf(offset), newData);
	}

	/**
	 * 替换指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param bytes 新数据
	 */
	public synchronized void replace(BigInteger offset, byte[] bytes) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (ArrayUtil.isEmpty(bytes)) {
			return;
		}
		this.delete(offset, bytes.length);
		this.insert(offset, bytes);
	}

	/**
	 * 替换指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param buffer 新数据
	 */
	public void replace(long offset, ChunkedByteBuffer buffer) {
		this.replace(BigInteger.valueOf(offset), buffer);
	}

	/**
	 * 替换指定范围的数据。
	 *
	 * @param offset 偏移量
	 * @param buffer 新数据
	 */
	public void replace(BigInteger offset, ChunkedByteBuffer buffer) {
		if (Objects.isNull(offset) || offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(buffer)) {
			return;
		}
		if (buffer == this) {
			synchronized (this) {
				List<byte[]> snapshot = this.snapshotBlocks();
				if (snapshot.isEmpty()) {
					return;
				}
				this.replaceSnapshot(offset, snapshot);
			}
			return;
		}
		List<byte[]> snapshot = buffer.snapshotBlocks();
		if (snapshot.isEmpty()) {
			return;
		}
		synchronized (this) {
			this.replaceSnapshot(offset, snapshot);
		}
	}

	/**
	 * 将数据写入输出流。
	 *
	 * @param out 输出流
	 */
	public synchronized void writeTo(OutputStream out) throws IOException {
		for (byte[] block : blocks) {
			out.write(block);
		}
	}

	/**
	 * 从输入流读取数据。
	 *
	 * @param in 输入流
	 * @return 读取字节数
	 */
	public BigInteger readFrom(InputStream in) throws IOException {
		return this.readFrom(in, true);
	}

	/**
	 * 从输入流读取数据。
	 *
	 * @param in 输入流
	 * @param closedIn 是否关闭输入流
	 * @return 读取字节数
	 */
	public BigInteger readFrom(InputStream in, boolean closedIn) throws IOException {
		return this.readFrom(in, BigInteger.ZERO, BigInteger.ONE.negate(), closedIn);
	}

	/**
	 * 从输入流读取数据。
	 *
	 * @param in 输入流
	 * @param skipBytes 跳过字节数
	 * @param maxBytes 最大字节数，负数表示不限
	 * @param closedIn 是否关闭输入流
	 * @return 读取字节数
	 */
	public synchronized BigInteger readFrom(InputStream in, BigInteger skipBytes, BigInteger maxBytes, boolean closedIn) throws IOException {
		Objects.requireNonNull(in, "InputStream cannot be null");
		if (Objects.nonNull(skipBytes) && skipBytes.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Skip bytes must be non-negative");
		}

		int bytesRead;
		byte[] bytes = new byte[maxBlockCapacity];
		try {
			if (Objects.nonNull(skipBytes) && skipBytes.compareTo(BigInteger.ZERO) > 0) {
				BigInteger totalSkipped = BigInteger.ZERO;
				while (totalSkipped.compareTo(skipBytes) < 0 &&
					(bytesRead = in.read(bytes, 0, Math.min(bytes.length, NumberUtil.toInt(skipBytes.subtract(totalSkipped))))) != -1) {
					totalSkipped = totalSkipped.add(BigInteger.valueOf(bytesRead));
				}
			}

			boolean ignoreMaxBytes = Objects.isNull(maxBytes) || maxBytes.compareTo(BigInteger.ZERO) < 0;
			BigInteger totalRead = BigInteger.ZERO;
			while ((ignoreMaxBytes || totalRead.compareTo(maxBytes) < 0) &&
				(bytesRead = in.read(bytes, 0, ignoreMaxBytes ? bytes.length : Math.min(bytes.length, NumberUtil.toInt(maxBytes.subtract(totalRead))))) != -1) {
				this.put(bytes, 0, bytesRead);
				totalRead = totalRead.add(BigInteger.valueOf(bytesRead));
			}
			return totalRead;
		} finally {
			if (closedIn) {
				IoUtil.close(in);
			}
		}
	}

	/**
	 * 比较两个缓冲区内容是否相同。
	 *
	 * @param other 另一个缓冲区
	 * @return 是否相同
	 */
	public boolean contentEquals(ChunkedByteBuffer other) {
		if (Objects.isNull(other)) {
			return false;
		}
		if (this == other) {
			return true;
		}
		List<byte[]> thisSnapshot = this.snapshotBlocks();
		List<byte[]> otherSnapshot = other.snapshotBlocks();
		if (calculateSize(thisSnapshot).compareTo(calculateSize(otherSnapshot)) != 0) {
			return false;
		}
		return contentEquals(thisSnapshot, otherSnapshot);
	}

	/**
	 * 创建缓冲区副本。
	 *
	 * @return 副本
	 */
	public synchronized ChunkedByteBuffer copy() {
		return this.copy(maxBlockCapacity);
	}

	/**
	 * 创建缓冲区副本。
	 *
	 * @param maxBlockCapacity 每块最大容量
	 * @return 副本
	 */
	public synchronized ChunkedByteBuffer copy(int maxBlockCapacity) {
		ChunkedByteBuffer copiedBuffer = new ChunkedByteBuffer(maxBlockCapacity);
		copiedBuffer.appendBlocks(this.snapshotBlocks());
		return copiedBuffer;
	}

	/**
	 * 判断缓冲区是否为空。
	 *
	 * @return 是否为空
	 */
	public synchronized boolean isEmpty() {
		return blocks.isEmpty() || this.size().compareTo(BigInteger.ZERO) == 0;
	}

	/**
	 * 清空数据。
	 */
	public synchronized void clear() {
		blocks.clear();
	}

	/**
	 * 获取块数量。
	 *
	 * @return 块数量
	 */
	public synchronized int blockSize() {
		return blocks.size();
	}

	/**
	 * 获取数据大小。
	 *
	 * @return 数据大小
	 */
	public synchronized BigInteger size() {
		return calculateSize(blocks);
	}

	protected synchronized List<byte[]> snapshotBlocks() {
		List<byte[]> snapshot = new ArrayList<>(blocks.size());
		for (byte[] block : blocks) {
			if (Objects.isNull(block) || block.length == 0) {
				continue;
			}
			snapshot.add(block.clone());
		}
		return snapshot;
	}

	protected void appendBlocks(List<byte[]> sourceBlocks) {
		for (byte[] block : sourceBlocks) {
			if (Objects.isNull(block) || block.length == 0) {
				continue;
			}
			this.put(block, 0, block.length);
		}
	}

	protected void insertSnapshot(BigInteger offset, List<byte[]> snapshot) {
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0) {
			this.appendBlocks(snapshot);
			return;
		}
		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		ChunkedByteBuffer secondPart = this.get(offset, size.subtract(offset));
		this.clear();
		this.appendBlocks(firstPart.blocks);
		this.appendBlocks(snapshot);
		this.appendBlocks(secondPart.blocks);
	}

	protected void replaceSnapshot(BigInteger offset, List<byte[]> snapshot) {
		this.delete(offset, calculateSize(snapshot));
		this.insertSnapshot(offset, snapshot);
	}

	protected ReadPosition findReadPosition(BigInteger offset) {
		BigInteger currentOffset = BigInteger.ZERO;
		for (int i = 0; i < blocks.size(); i++) {
			byte[] block = blocks.get(i);
			if (Objects.isNull(block) || block.length == 0) {
				continue;
			}
			BigInteger nextOffset = currentOffset.add(BigInteger.valueOf(block.length));
			if (offset.compareTo(currentOffset) >= 0 && offset.compareTo(nextOffset) < 0) {
				return new ReadPosition(i, offset.subtract(currentOffset).intValue());
			}
			currentOffset = nextOffset;
		}
		return null;
	}

	protected boolean contentEquals(List<byte[]> thisBlocks, List<byte[]> otherBlocks) {
		int thisBlockIndex = 0;
		int otherBlockIndex = 0;
		int thisBlockOffset = 0;
		int otherBlockOffset = 0;
		while (true) {
			while (thisBlockIndex < thisBlocks.size() && thisBlockOffset >= thisBlocks.get(thisBlockIndex).length) {
				thisBlockIndex++;
				thisBlockOffset = 0;
			}
			while (otherBlockIndex < otherBlocks.size() && otherBlockOffset >= otherBlocks.get(otherBlockIndex).length) {
				otherBlockIndex++;
				otherBlockOffset = 0;
			}
			boolean thisEnd = thisBlockIndex >= thisBlocks.size();
			boolean otherEnd = otherBlockIndex >= otherBlocks.size();
			if (thisEnd || otherEnd) {
				return thisEnd == otherEnd;
			}

			byte[] thisBlock = thisBlocks.get(thisBlockIndex);
			byte[] otherBlock = otherBlocks.get(otherBlockIndex);
			int thisAvailable = thisBlock.length - thisBlockOffset;
			int otherAvailable = otherBlock.length - otherBlockOffset;
			int compareLength = Math.min(thisAvailable, otherAvailable);
			for (int i = 0; i < compareLength; i++) {
				if (thisBlock[thisBlockOffset + i] != otherBlock[otherBlockOffset + i]) {
					return false;
				}
			}
			thisBlockOffset += compareLength;
			otherBlockOffset += compareLength;
		}
	}

	protected static int validateMaxBlockCapacity(int maxBlockCapacity) {
		if (maxBlockCapacity <= 0) {
			throw new IllegalArgumentException("Max block capacity must be positive");
		}
		return maxBlockCapacity;
	}

	protected static BigInteger calculateSize(List<byte[]> sourceBlocks) {
		BigInteger size = BigInteger.ZERO;
		for (byte[] block : sourceBlocks) {
			if (Objects.isNull(block)) {
				continue;
			}
			size = size.add(BigInteger.valueOf(block.length));
		}
		return size;
	}

	protected static final class ReadPosition {
		protected final int blockIndex;
		protected final int blockOffset;

		protected ReadPosition(int blockIndex, int blockOffset) {
			this.blockIndex = blockIndex;
			this.blockOffset = blockOffset;
		}
	}
}
