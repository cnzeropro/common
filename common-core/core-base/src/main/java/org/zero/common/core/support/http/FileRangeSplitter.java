package org.zero.common.core.support.http;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.DataSize;
import org.zero.common.core.extension.java.DataUnit;
import org.zero.common.core.extension.java.io.ChunkedByteBuffer;
import org.zero.common.core.support.http.header.RangeValue;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.NumberUtil;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/5
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class FileRangeSplitter {
	/**
	 * 默认缓冲区大小，8M
	 */
	protected final int DEFAULT_BUFFER_SIZE = 1 << 23;

	protected final RangeValue rangeValue;
	protected final File file;
	protected int bufferSize = DEFAULT_BUFFER_SIZE;

	@SneakyThrows
	public RangeChunkedBuffer getChunkedBuffer() {
		@Cleanup InputStream inputStream = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
		BigInteger size = IoUtil.getBigSizeExact(inputStream);
		DataUnit dataUnit = rangeValue.getDataUnit();
		List<RangeValue.Range> ranges = rangeValue.getRanges();
		BigInteger total = size.divide(dataUnit.getSize());
		RangeChunkedBuffer rangeChunkedBuffer = new RangeChunkedBuffer(dataUnit, total, ranges.size());
		for (RangeValue.Range range : ranges) {
			ChunkedByteBuffer chunkedByteBuffer;
			RangeValue.Type rangeType = range.getType();
			if (rangeType == RangeValue.Type.BETWEEN) {
				BigInteger start = range.getStart();
				BigInteger end = range.getEnd();
				BigInteger between = end.subtract(start);
				BigInteger offset = between.add(BigInteger.ONE);
				BigInteger startBytes = DataSize.of(start, dataUnit).toBytes();
				BigInteger offsetBytes = DataSize.of(offset, dataUnit).toBytes();
				chunkedByteBuffer = this.read(startBytes, offsetBytes, size);
			} else if (rangeType == RangeValue.Type.FROM) {
				BigInteger start = range.getStart();
				BigInteger startBytes = DataSize.of(start, dataUnit).toBytes();
				chunkedByteBuffer = this.read(startBytes, null, size);
			} else if (rangeType == RangeValue.Type.LAST) {
				BigInteger offset = range.getOffset();
				BigInteger suffixBytes = DataSize.of(offset, dataUnit).toBytes();
				chunkedByteBuffer = this.read(suffixBytes, size);
			} else {
				throw new IllegalArgumentException("Invalid slice type: " + rangeType);
			}
			rangeChunkedBuffer.buffers.put(range, chunkedByteBuffer);
		}

		return rangeChunkedBuffer;
	}

	/**
	 * 读取数据
	 *
	 * @param start  开始位置，从 0 开始
	 * @param offset 偏移量
	 * @return 数据
	 */
	@SneakyThrows
	protected ChunkedByteBuffer read(BigInteger start, BigInteger offset, BigInteger total) {
		if (start.compareTo(total) > 0) {
			throw new IndexOutOfBoundsException(String.format("Range start[%s] > total[%s]", start, total));
		}
		if (Objects.nonNull(offset) && start.add(offset).compareTo(total) > 0) {
			throw new IndexOutOfBoundsException(String.format("Range start[%s] + offset[%s] > total[%s]", start, offset, total));
		}
		@Cleanup InputStream inputStream = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
		byte[] buffer = new byte[bufferSize];
		int bytesRead;
		// 跳过数据
		BigInteger totalSkipped = BigInteger.ZERO;
		while (totalSkipped.compareTo(start) < 0 && (bytesRead = inputStream.read(buffer, 0, Math.min(buffer.length, NumberUtil.toInt(start.subtract(totalSkipped))))) != -1) {
			totalSkipped = totalSkipped.add(BigInteger.valueOf(bytesRead));
		}
		// 读取数据
		ChunkedByteBuffer chunkedByteBuffer = new ChunkedByteBuffer();
		BigInteger totalRead = BigInteger.ZERO;
		while ((Objects.isNull(offset) || totalRead.compareTo(offset) < 0) && (bytesRead = inputStream.read(buffer, 0, Objects.isNull(offset) ? buffer.length : Math.min(buffer.length, NumberUtil.toInt(offset.subtract(totalRead))))) != -1) {
			totalRead = totalRead.add(BigInteger.valueOf(bytesRead));
			chunkedByteBuffer.put(buffer, 0, bytesRead);
		}
		return chunkedByteBuffer;
	}

	/**
	 * 读取数据
	 *
	 * @param suffix 最后长度的字节数
	 * @return 数据
	 */
	@SneakyThrows
	protected ChunkedByteBuffer read(BigInteger suffix, BigInteger total) {
		BigInteger start = total.subtract(suffix);
		if (start.compareTo(BigInteger.ZERO) < 0) {
			throw new IndexOutOfBoundsException("Invalid range start: " + start);
		}
		return this.read(start, null, total);
	}

	@Getter
	public static class RangeChunkedBuffer {
		protected final DataUnit dataUnit;
		protected final BigInteger total;
		protected final Map<RangeValue.Range, ChunkedByteBuffer> buffers;

		protected RangeChunkedBuffer(DataUnit dataUnit, BigInteger total, int bufferSize) {
			this.dataUnit = dataUnit;
			this.total = total;
			buffers = new LinkedHashMap<>(bufferSize, 1.0F);
		}
	}
}
