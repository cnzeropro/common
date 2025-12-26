package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.util.unit.DataUnit.BYTES;
import static org.springframework.util.unit.DataUnit.GIGABYTES;
import static org.springframework.util.unit.DataUnit.KILOBYTES;
import static org.springframework.util.unit.DataUnit.MEGABYTES;
import static org.springframework.util.unit.DataUnit.TERABYTES;

/**
 *
 * Range 标头值
 * <p>
 * HTTP 规范中主要使用 bytes 作为单位，其他虽然没有限制，但并不推荐
 *
 * @author zero
 * @see org.springframework.http.HttpRange
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Range">Range</a>
 * @since 2022/11/21
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RangeValue {
	public static final String HEADER_NAME = "Range";

	protected static final String RANGE_REGEX = "^.*bytes\\s*=\\s*((?:\\d+-\\d*|-?\\d+)(?:,\\s*(?:\\d+-\\d*|-?\\d+))*)$";
	protected static final Pattern RANGE_PATTERN = Pattern.compile(RANGE_REGEX);

	/**
	 * 数据单位
	 */
	protected final String dataUnit;
	/**
	 * 资源总大小
	 */
	protected long total = 0L;
	/**
	 * 资源总大小（byte）
	 */
	protected long totalByte = 0L;
	/**
	 * 切片列表
	 */
	protected List<Slice> slices = new ArrayList<>();

	public static RangeValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		// Range 请求头内容格式不匹配（如果前端按照 RFC 7233 规范传入则一般不会）
		if (!RANGE_PATTERN.matcher(text).matches()) {
			throw new IllegalArgumentException("Invalid range: " + text);
		}
		String[] parts = StringUtil.split(text.toString(), StringPool.EQUAL, true).toArray(new String[0]);
		if (parts.length != 2) {
			throw new IllegalArgumentException(text + " is invalid");
		}
		String dataUnit = parts[0];
		Collection<String> dataRanges = StringUtil.split(parts[1], StringPool.COMMA, true);
		// 生成Range信息
		RangeValue rangeValue = new RangeValue(dataUnit);
		List<Slice> slices = dataRanges.stream()
			.map(dataRange -> {
				String[] ranges = StringUtil.split(dataRange, StringPool.HYPHEN, true).toArray(new String[0]);
				if (ranges.length != 2) {
					throw new IllegalArgumentException(dataRange + " is invalid");
				}
				// 开始位置
				long start = 0L;
				if (StringUtils.hasText(ranges[0])) {
					start = Long.parseLong(ranges[0]);
				}
				// 结束位置
				long end = -1L;
				if (StringUtils.hasText(ranges[1])) {
					end = Long.parseLong(ranges[1]);
				}
				return rangeValue.new Slice(start, end);
			})
			.collect(Collectors.toList());
		rangeValue.setSlices(slices);
		return rangeValue;
	}

	private void setSlices(List<Slice> slices) {
		this.slices = slices;
	}

	public RangeValue setTotalByte(long totalByte) {
		this.totalByte = totalByte;
		return setTotalFromByte(totalByte);
	}

	/**
	 * 使用字节总数和单位来设置资源总大小
	 */
	private RangeValue setTotalFromByte(long totalByte) {
		DataSize dataSize = DataSize.ofBytes(totalByte);
		if (BYTES.name().equalsIgnoreCase(dataUnit)) {
			this.total = dataSize.toBytes();
		} else if (KILOBYTES.name().equalsIgnoreCase(dataUnit)) {
			this.total = dataSize.toKilobytes();
		} else if (MEGABYTES.name().equalsIgnoreCase(dataUnit)) {
			this.total = dataSize.toGigabytes();
		} else if (GIGABYTES.name().equalsIgnoreCase(dataUnit)) {
			this.total = dataSize.toGigabytes();
		} else if (TERABYTES.name().equalsIgnoreCase(dataUnit)) {
			this.total = dataSize.toTerabytes();
		} else {
			throw new IllegalArgumentException("Unsupported unit: " + dataUnit);
		}
		return this;
	}

	/**
	 * 切片
	 */
	public  class Slice {
		/**
		 * 开始位置
		 */
		@Getter
		protected long start = 0L;
		/**
		 * 结束位置
		 */
		protected long end = -1L;

		/**
		 * 获取开始位置（byte）
		 */
		public long getStartByte() {
			return getByte(start);
		}

		/**
		 * 获取结束位置（byte）
		 */
		public long getEndByte() {
			// 如果结束位置未指定，默认使用资源的总大小
			if (end == -1L) {
				return getTotalByte();
			}
			return getByte(end);
		}

		/**
		 * 获取结束位置
		 */
		public long getEnd() {
			// 如果结束位置未指定，默认使用资源的总大小
			if (end == -1L) {
				end = getTotal();
			}
			return end;
		}

		/**
		 * 根据数据长度和单位计算出字节数
		 */
		protected long getByte(long length) {
			DataSize dataSize;
			if (BYTES.name().equalsIgnoreCase(dataUnit)) {
				dataSize = DataSize.ofBytes(length);
			} else if (KILOBYTES.name().equalsIgnoreCase(dataUnit)) {
				dataSize = DataSize.ofKilobytes(length);
			} else if (MEGABYTES.name().equalsIgnoreCase(dataUnit)) {
				dataSize = DataSize.ofMegabytes(length);
			} else if (GIGABYTES.name().equalsIgnoreCase(dataUnit)) {
				dataSize = DataSize.ofGigabytes(length);
			} else if (TERABYTES.name().equalsIgnoreCase(dataUnit)) {
				dataSize = DataSize.ofTerabytes(length);
			} else {
				throw new IllegalArgumentException("Unsupported DataUnit: " + dataUnit);
			}
			return dataSize.toBytes();
		}

		protected Slice(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}
}
