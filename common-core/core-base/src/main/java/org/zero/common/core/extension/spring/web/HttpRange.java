package org.zero.common.core.extension.spring.web;

import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.Arrays;
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
 * Range 信息
 * <p>
 * HTTP 规范中主要使用 bytes 作为单位，其他虽然没有限制，但并不推荐
 *
 * @author zero
 * @see org.springframework.http.HttpRange
 * @since 2022/11/21
 */
@Getter
public class HttpRange {
	public static final String RANGE_REGEX = "^.*bytes\\s*=\\s*((?:\\d+-\\d*|-?\\d+)(?:,\\s*(?:\\d+-\\d*|-?\\d+))*)$";
	private static final Pattern RANGE_PATTERN = Pattern.compile(RANGE_REGEX);

	/**
	 * 数据单位
	 */
	private final String dataUnit;
	/**
	 * 资源总大小
	 */
	private long total = 0L;
	/**
	 * 资源总大小（byte）
	 */
	private long totalByte = 0L;
	/**
	 * 切片列表
	 */
	private List<Slice> slices = new ArrayList<>();

	/**
	 * 解析 Range 请求头
	 */
	public static HttpRange parse(String range) {
		// 不存在 Range 请求头
		if (!StringUtils.hasText(range)) {
			return null;
		}
		// Range 请求头内容格式不匹配（如果前端按照 RFC 7233 规范传入则一般不会）
		if (!RANGE_PATTERN.matcher(range).matches()) {
			throw new IllegalArgumentException("Invalid range: " + range);
		}
		// Range请求头解析
		// Range请求头示例：bytes=0-50,-150,300-
		// 正则已效验，不会出现没有“=”的情况，因此无需进行-1判断
		int indexOf = range.indexOf("=");
		String dataUnit = StringUtils.trimWhitespace(range.substring(0, indexOf));
		String allDataRange = StringUtils.trimWhitespace(range.substring(indexOf + 1));
		String[] dataRanges = StringUtils.commaDelimitedListToStringArray(allDataRange);
		// 生成Range信息
		HttpRange httpRange = new HttpRange(dataUnit);
		List<Slice> slices = Arrays.stream(dataRanges)
			.map(dataRange -> {
				// 数据范围解析
				int index = dataRange.indexOf("-");
				String startStr;
				String endStr;
				if (index < 0) {
					startStr = StringUtils.trimWhitespace(dataRange);
					endStr = null;
				} else {
					startStr = StringUtils.trimWhitespace(dataRange.substring(0, index));
					endStr = StringUtils.trimWhitespace(dataRange.substring(index + 1));
				}
				// 开始和结束位置均未指定（”-“的情况）（已做正则匹配，基本不可能出现这种情况）
				if (!StringUtils.hasText(startStr) && !StringUtils.hasText(endStr)) {
					throw new IllegalArgumentException("Invalid range slice: " + dataRange);
				}
				// 开始位置
				long start = 0L;
				if (StringUtils.hasText(startStr)) {
					start = Long.parseLong(startStr);
				}
				// 结束位置
				long end = -1L;
				if (StringUtils.hasText(endStr)) {
					end = Long.parseLong(endStr);
				}
				return httpRange.new Slice(start, end);
			})
			.collect(Collectors.toList());
		httpRange.setSlices(slices);
		return httpRange;
	}

	private HttpRange(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	private void setSlices(List<Slice> slices) {
		this.slices = slices;
	}

	public HttpRange setTotalByte(long totalByte) {
		this.totalByte = totalByte;
		return setTotalFromByte(totalByte);
	}

	/**
	 * 使用字节总数和单位来设置资源总大小
	 */
	private HttpRange setTotalFromByte(long totalByte) {
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
			throw new IllegalArgumentException("Unsupported DataUnit: " + dataUnit);
		}
		return this;
	}

	/**
	 * 切片
	 */
	public class Slice {
		/**
		 * 开始位置
		 */
		@Getter
		private long start = 0L;
		/**
		 * 结束位置
		 */
		private long end = -1L;

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
		private long getByte(long length) {
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

		private Slice(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}
}
