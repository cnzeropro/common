package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.extension.java.DataUnit;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
@EqualsAndHashCode
@RequiredArgsConstructor
public class RangeValue {
	public static final HttpHeader HEADER = HttpHeader.RANGE;

	protected static final String RANGE_REGEX = "^.*bytes\\s*=\\s*((?:\\d+-\\d*|-?\\d+)(?:,\\s*(?:\\d+-\\d*|-?\\d+))*)$";
	protected static final Pattern RANGE_PATTERN = Pattern.compile(RANGE_REGEX);

	/**
	 * 数据单位
	 */
	protected final DataUnit dataUnit;
	/**
	 * 切片列表
	 */
	protected final List<Range> ranges;

	public static Builder builder() {
		return new Builder();
	}

	public static RangeValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		// Range 请求头内容格式不匹配（如果按照 RFC 7233 规范传入则一般不会）
		if (!RANGE_PATTERN.matcher(text).matches()) {
			throw new IllegalArgumentException("Invalid range: " + text);
		}
		String[] parts = StringUtil.splitToArray(text.toString(), StringPool.EQUAL, true);
		if (parts.length != 2) {
			throw new IllegalArgumentException(text + " is invalid");
		}
		// 去除后缀“s”以匹配数据单位
		String dataUnitName = StringUtil.removeSuffix(parts[0], "s", true);
		DataUnit dataUnit = DataUnit.fromName(dataUnitName);
		List<Range> ranges = StringUtil.split(parts[1], StringPool.COMMA, true)
			.stream()
			.map(dataRange -> {
				String[] dataRanges = StringUtil.splitToArray(dataRange, StringPool.HYPHEN, true);
				if (dataRanges.length != 2) {
					throw new IllegalArgumentException(dataRange + " is invalid");
				}
				if (CharSequenceUtil.nonBlank(dataRanges[0])) {
					BigInteger start = new BigInteger(dataRanges[0]);
					if (CharSequenceUtil.nonBlank(dataRanges[1])) {
						BigInteger end = new BigInteger(dataRanges[1]);
						if (start.compareTo(end) > 0) {
							throw new IndexOutOfBoundsException(String.format("start[%s] > end[%s]", start, end));
						}
						return Range.ofBetween(start, end);
					}
					return Range.ofFrom(start);
				}
				if (CharSequenceUtil.nonBlank(dataRanges[1])) {
					BigInteger offset = new BigInteger(dataRanges[1]);
					return Range.ofLast(offset);
				}
				throw new IllegalArgumentException("Invalid range: " + dataRange);
			})
			.collect(Collectors.toList());
		return new RangeValue(dataUnit, ranges);
	}

	@Override
	public String toString() {
		StringJoiner stringJoiner = new StringJoiner(StringPool.COMMA + StringPool.SPACE,
			dataUnit.name().toLowerCase() + "s",
			StringPool.EMPTY);
		ranges.forEach(range -> stringJoiner.add(range.toString()));
		return stringJoiner.toString();
	}

	/**
	 * 切片
	 */
	@Getter
	@EqualsAndHashCode
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Range {
		protected BigInteger start;
		protected BigInteger offset;
		protected BigInteger end;
		protected Type type;

		public static Range ofBetween(BigInteger start, BigInteger end) {
			return new Range(start, null, end, Type.BETWEEN);
		}

		public static Range ofFrom(BigInteger start) {
			return new Range(start, null, null, Type.FROM);
		}

		public static Range ofLast(BigInteger offset) {
			return new Range(null, offset, null, Type.LAST);
		}

		@Override
		public String toString() {
			if (type == Type.BETWEEN) {
				return start + StringPool.HYPHEN + end;
			}
			if (type == Type.FROM) {
				return start + StringPool.HYPHEN;
			}
			if (type == Type.LAST) {
				return StringPool.HYPHEN + offset;
			}
			return StringPool.EMPTY;
		}
	}

	/**
	 * 范围类型
	 */
	public enum Type {
		/**
		 * 完整范围
		 *
		 * @see Range#start
		 * @see Range#end
		 */
		BETWEEN,
		/**
		 * 从指定位置到结尾
		 *
		 * @see Range#start
		 */
		FROM,
		/**
		 * 最后 N 个字节
		 *
		 * @see Range#offset
		 */
		LAST,
	}

	public static class Builder {
		protected DataUnit dataUnit;
		protected final List<Range> ranges = new ArrayList<>();

		public Builder dataUnit(DataUnit dataUnit) {
			this.dataUnit = dataUnit;
			return this;
		}

		public Builder ranges(Collection<Range> ranges) {
			this.ranges.addAll(ranges);
			return this;
		}

		public Builder range(Range range) {
			this.ranges.add(range);
			return this;
		}

		public RangeValue build() {
			return new RangeValue(dataUnit, ranges);
		}
	}
}
