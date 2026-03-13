package org.zero.common.core.extension.java.io;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.NumberUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 分块字节缓冲区
 * <p>
 * 性能较差，如果存放数据小于 2GiB 时，建议使用 {@link java.nio.ByteBuffer}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/22
 */
@RequiredArgsConstructor
public class ChunkedByteBuffer implements Serializable {
	/**
	 * 数据块
	 */
	protected final List<byte[]> blocks = new LinkedList<>();
	/**
	 * 每块的最大容量
	 */
	protected final int maxBlockCapacity;

	public ChunkedByteBuffer() {
		this(ArrayUtil.SAFE_MAX_ARRAY_SIZE);
	}

	/**
	 * 添加数据
	 * <p>
	 * 注意：此方法效率低下，如果存在添加大量数据的需求，请使用 {@link #put(byte[])}
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
		// 块已满，分配新块
		if (lastBlockLength >= maxBlockCapacity) {
			byte[] block = new byte[1];
			block[0] = b;
			blocks.add(block);
			return;
		}
		// 块未满，扩展块
		byte[] block = new byte[lastBlockLength + 1];
		System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
		block[lastBlockLength] = b;
		blocks.set(lastBlockIndex, block);
	}

	/**
	 * 添加数据
	 *
	 * @param bytes 数据
	 */
	public void put(byte[] bytes) {
		this.put(bytes, 0, bytes.length);
	}

	/**
	 * 添加数据
	 *
	 * @param bytes  数据
	 * @param offset 数据偏移量
	 */
	public void put(byte[] bytes, int offset) {
		this.put(bytes, offset, bytes.length - offset);
	}

	/**
	 * 添加数据
	 *
	 * @param bytes  数据
	 * @param offset 数据偏移量
	 * @param length 数据长度
	 */
	public synchronized void put(byte[] bytes, int offset, int length) {
		if (offset < 0 || length < 0 || offset + length > bytes.length) {
			throw new IndexOutOfBoundsException();
		}
		if (blocks.isEmpty()) {
			// 一个数据块可以容纳，直接创建并写入
			if (length <= maxBlockCapacity) {
				byte[] block = new byte[length];
				System.arraycopy(bytes, offset, block, 0, length);
				blocks.add(block);
				return;
			}
			// 一个数据块无法容纳，持续创建新块分片存储，直到没有剩余
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
		// 当前位置块可以容纳，扩容块并存储
		if (length <= lastBlockRemaining) {
			byte[] block = new byte[lastBlockLength + length];
			System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
			System.arraycopy(bytes, offset, block, lastBlockLength, length);
			blocks.set(lastBlockIndex, block);
			return;
		}
		// 当前位置块无法容纳，将容量扩展到最大并写入一部分
		if (lastBlockRemaining > 0) {
			byte[] block = new byte[maxBlockCapacity];
			System.arraycopy(lastBlock, 0, block, 0, lastBlockLength);
			System.arraycopy(bytes, offset, block, lastBlockLength, lastBlockRemaining);
			blocks.set(lastBlockIndex, block);
			offset += lastBlockRemaining;
			length -= lastBlockRemaining;
		}
		// 然后持续创建新的块，写入数据，直到没有剩余
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
	 * 添加数据
	 *
	 * @param buffer 数据
	 */
	public synchronized void put(ChunkedByteBuffer buffer) {
		if (Objects.isNull(buffer)) {
			return;
		}
		buffer.blocks.forEach(this::put);
	}

	/**
	 * 获取数据
	 *
	 * @param bytes 数据容器
	 * @return 写入的字节数
	 */
	public int get(byte[] bytes) {
		return this.get(BigInteger.ZERO, bytes);
	}

	/**
	 * 获取数据
	 *
	 * @param offset 偏移量
	 * @param bytes  数据容器
	 * @return 写入的字节数
	 */
	public int get(long offset, byte[] bytes) {
		return this.get(BigInteger.valueOf(offset), bytes);
	}

	/**
	 * 获取数据
	 *
	 * @param offset 偏移量
	 * @param bytes  数据容器
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
		return this.get(offset, bytes, 0, bytes.length);
	}

	/**
	 * 获取数据
	 *
	 * @param length 长度
	 * @return 数据
	 */
	public byte[] get(int length) {
		return this.get(BigInteger.ZERO, length);
	}

	/**
	 * 获取数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public byte[] get(long offset, int length) {
		return this.get(BigInteger.valueOf(offset), length);
	}

	/**
	 * 获取数据
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
		// 调整长度，确保不会超过缓冲区末尾
		BigInteger remaining = size.subtract(offset);
		if (BigInteger.valueOf(length).compareTo(remaining) > 0) {
			length = remaining.intValue();
		}
		byte[] result = new byte[length];
		this.get(offset, result, 0, length);
		return result;
	}

	protected int get(BigInteger offset, byte[] dest, int destOffset, int length) {
		// 定位位置
		BigInteger currentOffset = BigInteger.ZERO;
		int currentBlockIndex = 0;
		int currentBlockOffset = 0;
		for (int i = 0; i < blocks.size(); i++) {
			byte[] block = blocks.get(i);
			BigInteger nextOffset = currentOffset.add(BigInteger.valueOf(block.length));
			if (offset.compareTo(currentOffset) >= 0 && offset.compareTo(nextOffset) < 0) {
				currentBlockIndex = i;
				currentBlockOffset = offset.subtract(currentOffset).intValue();
				break;
			}
			currentOffset = nextOffset;
		}
		// 读取数据
		int bytesRead = 0;
		int resultCurrentOffset = destOffset;
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
	 * 获取数据
	 *
	 * @param offset 偏移量
	 * @return 数据
	 */
	public ChunkedByteBuffer get(long offset) {
		return this.get(BigInteger.valueOf(offset));
	}


	/**
	 * 获取数据
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
			return new ChunkedByteBuffer();
		}
		return this.get(offset, size.subtract(offset));
	}

	/**
	 * 获取数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 数据
	 */
	public ChunkedByteBuffer get(long offset, BigInteger length) {
		return this.get(BigInteger.valueOf(offset), length);
	}

	/**
	 * 获取数据
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

		// 调整长度，确保不会超过缓冲区末尾
		BigInteger remaining = size.subtract(offset);
		if (length.compareTo(remaining) > 0) {
			length = remaining;
		}
		// 定位位置
		BigInteger currentOffset = BigInteger.ZERO;
		int currentBlockIndex = 0;
		int currentBlockOffset = 0;
		for (int i = 0; i < blocks.size(); i++) {
			byte[] block = blocks.get(i);
			BigInteger nextOffset = currentOffset.add(BigInteger.valueOf(block.length));
			if (offset.compareTo(currentOffset) >= 0 && offset.compareTo(nextOffset) < 0) {
				currentBlockIndex = i;
				currentBlockOffset = offset.subtract(currentOffset).intValue();
				break;
			}
			currentOffset = nextOffset;
		}
		// 读取数据
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
	 * 在指定位置插入数据
	 *
	 * @param offset 偏移量
	 * @param bytes  插入数据
	 */
	public synchronized void insert(long offset, byte[] bytes) {
		this.insert(BigInteger.valueOf(offset), bytes);
	}

	/**
	 * 在指定位置插入数据
	 *
	 * @param offset 偏移量
	 * @param bytes  插入数据
	 */
	public synchronized void insert(BigInteger offset, byte[] bytes) {
		if (offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(bytes) || bytes.length == 0) {
			return;
		}
		BigInteger size = this.size();
		// 如果偏移量超过当前大小，直接追加
		if (offset.compareTo(size) >= 0) {
			this.put(bytes);
			return;
		}

		// 分割缓冲区，插入数据，然后重新合并
		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		ChunkedByteBuffer secondPart = this.get(offset, size.subtract(offset));
		this.clear();
		this.put(firstPart);
		this.put(bytes);
		this.put(secondPart);
	}

	/**
	 * 在指定位置插入数据
	 *
	 * @param offset 偏移量
	 * @param buffer 插入数据
	 */
	public void insert(long offset, ChunkedByteBuffer buffer) {
		this.insert(BigInteger.valueOf(offset), buffer);
	}

	/**
	 * 在指定位置插入数据
	 *
	 * @param offset 偏移量
	 * @param buffer 插入数据
	 */
	public synchronized void insert(BigInteger offset, ChunkedByteBuffer buffer) {
		if (offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(buffer) || buffer.isEmpty()) {
			return;
		}
		BigInteger size = this.size();
		// 如果偏移量超过当前大小，直接追加
		if (offset.compareTo(size) >= 0) {
			this.put(buffer);
			return;
		}

		// 分割缓冲区，插入数据，然后重新合并
		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		ChunkedByteBuffer secondPart = this.get(offset, size.subtract(offset));
		this.clear();
		this.put(firstPart);
		this.put(buffer);
		this.put(secondPart);
	}

	/**
	 * 删除指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(BigInteger offset, long length) {
		this.delete(offset, BigInteger.valueOf(length));
	}

	/**
	 * 删除指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(long offset, BigInteger length) {
		this.delete(BigInteger.valueOf(offset), length);
	}

	/**
	 * 删除指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public void delete(long offset, long length) {
		this.delete(BigInteger.valueOf(offset), BigInteger.valueOf(length));
	}

	/**
	 * 删除指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param length 长度
	 */
	public synchronized void delete(BigInteger offset, BigInteger length) {
		if (offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (length.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Length must be non-negative");
		}
		BigInteger size = this.size();
		if (offset.compareTo(size) >= 0 || length.equals(BigInteger.ZERO)) {
			return;
		}

		// 调整长度，确保不会超过缓冲区末尾
		BigInteger remaining = size.subtract(offset);
		if (length.compareTo(remaining) > 0) {
			length = remaining;
		}

		// 保留删除范围之前和之后的数据
		ChunkedByteBuffer firstPart = this.get(BigInteger.ZERO, offset);
		BigInteger secondPartOffset = offset.add(length);
		ChunkedByteBuffer secondPart = this.get(secondPartOffset, size.subtract(secondPartOffset));
		this.clear();
		this.put(firstPart);
		this.put(secondPart);
	}

	/**
	 * 替换指定范围的数据
	 *
	 * @param offset  偏移量
	 * @param newData 新数据
	 */
	public void replace(long offset, byte[] newData) {
		this.replace(BigInteger.valueOf(offset), newData);
	}

	/**
	 * 替换指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param bytes  新数据
	 */
	public synchronized void replace(BigInteger offset, byte[] bytes) {
		if (offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (ArrayUtil.isEmpty(bytes)) {
			return;
		}
		this.delete(offset, bytes.length);
		this.insert(offset, bytes);
	}

	/**
	 * 替换指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param buffer 新数据
	 */
	public synchronized void replace(long offset, ChunkedByteBuffer buffer) {
		this.replace(BigInteger.valueOf(offset), buffer);
	}

	/**
	 * 替换指定范围的数据
	 *
	 * @param offset 偏移量
	 * @param buffer 新数据
	 */
	public synchronized void replace(BigInteger offset, ChunkedByteBuffer buffer) {
		if (offset.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		if (Objects.isNull(buffer)) {
			return;
		}
		this.delete(offset, buffer.size());
		this.insert(offset, buffer);
	}

	/**
	 * 将数据写入输出流
	 *
	 * @param out 输出流
	 */
	public synchronized void writeTo(OutputStream out) throws IOException {
		for (byte[] block : blocks) {
			out.write(block);
		}
	}

	/**
	 * 从输入流中读取数据
	 *
	 * @param in 输入流
	 */
	public BigInteger readFrom(InputStream in) throws IOException {
		return this.readFrom(in, true);
	}


	/**
	 * 从输入流中读取数据
	 *
	 * @param in       输入流
	 * @param closedIn 是否关闭输入流
	 */
	public BigInteger readFrom(InputStream in, boolean closedIn) throws IOException {
		return this.readFrom(in, BigInteger.ZERO, BigInteger.ONE.negate(), closedIn);
	}

	/**
	 * 从输入流中读取数据
	 *
	 * @param in        输入流
	 * @param skipBytes 跳过字节数
	 * @param maxBytes  最大字节数
	 * @param closedIn  是否关闭输入流
	 */
	public synchronized BigInteger readFrom(InputStream in, BigInteger skipBytes, BigInteger maxBytes, boolean closedIn) throws IOException {
		int bytesRead;
		byte[] bytes = new byte[maxBlockCapacity];
		// 跳过字节，不使用 skip 相关方法来跳过（虽然 skip 实现也是用的 read，但子类实现或许不太一样），在大数据流或者其他网络流时可能遇到问题
		if (Objects.nonNull(skipBytes) && skipBytes.compareTo(BigInteger.ZERO) > 0) {
			BigInteger totalSkipped = BigInteger.ZERO;
			while (totalSkipped.compareTo(skipBytes) < 0 &&
				(bytesRead = in.read(bytes, 0, Math.min(bytes.length, NumberUtil.toInt(skipBytes.subtract(totalSkipped))))) != -1) {
				totalSkipped = totalSkipped.add(BigInteger.valueOf(bytesRead));
			}
		}
		// 读取数据
		boolean ignoreMaxBytes = Objects.isNull(maxBytes) || maxBytes.compareTo(BigInteger.ZERO) < 0;
		BigInteger totalRead = BigInteger.ZERO;
		while ((ignoreMaxBytes || totalRead.compareTo(maxBytes) < 0) &&
			(bytesRead = in.read(bytes, 0, ignoreMaxBytes ? bytes.length : Math.min(bytes.length, NumberUtil.toInt(maxBytes.subtract(totalRead))))) != -1) {
			this.put(bytes, 0, bytesRead);
			totalRead = totalRead.add(BigInteger.valueOf(bytesRead));
		}
		// 关闭输入流
		if (closedIn) {
			IoUtil.close(in);
		}
		return totalRead;
	}

	/**
	 * 比较两个缓冲区的内容是否相同
	 *
	 * @param other 另一个缓冲区
	 */
	public synchronized boolean contentEquals(ChunkedByteBuffer other) {
		if (Objects.isNull(other)) {
			return false;
		}
		if (this == other) {
			return true;
		}
		BigInteger thisSize = this.size();
		BigInteger otherSize = other.size();
		if (thisSize.compareTo(otherSize) != 0) {
			return false;
		}

		// 逐块比较
		BigInteger currentOffset = BigInteger.ZERO;
		int thisBlockIndex = 0, otherBlockIndex = 0;
		int thisBlockOffset = 0, otherBlockOffset = 0;
		while (currentOffset.compareTo(thisSize) < 0) {
			byte[] thisBlock = blocks.get(thisBlockIndex);
			byte[] otherBlock = other.blocks.get(otherBlockIndex);

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

			if (thisBlockOffset >= thisBlock.length) {
				thisBlockIndex++;
				thisBlockOffset = 0;
			}
			if (otherBlockOffset >= otherBlock.length) {
				otherBlockIndex++;
				otherBlockOffset = 0;
			}

			currentOffset = currentOffset.add(BigInteger.valueOf(compareLength));
		}
		return true;
	}

	/**
	 * 创建缓冲区副本
	 */
	public synchronized ChunkedByteBuffer copy() {
		return this.copy(maxBlockCapacity);
	}

	/**
	 * 创建缓冲区副本
	 *
	 * @param maxBlockCapacity 每块最大容量
	 */
	public synchronized ChunkedByteBuffer copy(int maxBlockCapacity) {
		ChunkedByteBuffer copiedBuffer = new ChunkedByteBuffer(maxBlockCapacity);
		for (byte[] block : blocks) {
			copiedBuffer.put(block.clone());
		}
		return copiedBuffer;
	}

	/**
	 * 判断缓冲区是否为空
	 *
	 * @return 缓冲区是否为空
	 */
	public synchronized boolean isEmpty() {
		return blocks.isEmpty() || this.size().compareTo(BigInteger.ZERO) == 0;
	}

	/**
	 * 清空数据
	 */
	public synchronized void clear() {
		blocks.clear();
	}

	/**
	 * 获取块数量
	 */
	public synchronized int blockSize() {
		return blocks.size();
	}

	/**
	 * 获取数据大小（字节数）
	 *
	 * @return 数据大小（字节数）
	 */
	public synchronized BigInteger size() {
		BigInteger size = BigInteger.ZERO;
		for (byte[] block : blocks) {
			size = size.add(BigInteger.valueOf(block.length));
		}
		return size;
	}
}
