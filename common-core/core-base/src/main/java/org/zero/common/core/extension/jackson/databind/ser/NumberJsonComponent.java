package org.zero.common.core.extension.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/31
 */
@JsonComponent
public class NumberJsonComponent {
	private static boolean isSafeInteger(long value) {
		return value >= LongSerializer.JS_MIN_SAFE_INTEGER && value <= LongSerializer.JS_MAX_SAFE_INTEGER;
	}

	private static boolean isSafeInteger(BigInteger value) {
		return value.compareTo(BigIntegerSerializer.JS_MIN_SAFE_INTEGER) >= 0
				&& value.compareTo(BigIntegerSerializer.JS_MAX_SAFE_INTEGER) <= 0;
	}

	private static boolean isSafeInteger(BigDecimal value) {
		BigDecimal normalized = value.stripTrailingZeros();
		return normalized.scale() <= 0
				&& normalized.compareTo(BigDecimalSerializer.JS_MIN_SAFE_INTEGER) >= 0
				&& normalized.compareTo(BigDecimalSerializer.JS_MAX_SAFE_INTEGER) <= 0;
	}

	public static class LongSerializer extends StdSerializer<Long> {
		public static final long JS_MAX_SAFE_INTEGER = (1L << 53L) - 1L;
		public static final long JS_MIN_SAFE_INTEGER = -JS_MAX_SAFE_INTEGER;

		public LongSerializer() {
			super(Long.class);
		}

		@Override
		public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (Objects.isNull(value)) {
				gen.writeNull();
				return;
			}
			if (isSafeInteger(value)) {
				gen.writeNumber(value);
			} else {
				gen.writeString(value.toString());
			}
		}
	}

	public static class BigIntegerSerializer extends StdSerializer<BigInteger> {
		public static final BigInteger JS_MAX_SAFE_INTEGER = BigInteger.valueOf(LongSerializer.JS_MAX_SAFE_INTEGER);
		public static final BigInteger JS_MIN_SAFE_INTEGER = BigInteger.valueOf(LongSerializer.JS_MIN_SAFE_INTEGER);

		public BigIntegerSerializer() {
			super(BigInteger.class);
		}

		@Override
		public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (Objects.isNull(value)) {
				gen.writeNull();
				return;
			}
			if (isSafeInteger(value)) {
				gen.writeNumber(value);
			} else {
				gen.writeString(value.toString());
			}
		}
	}

	public static class BigDecimalSerializer extends StdSerializer<BigDecimal> {
		public static final BigDecimal JS_MAX_SAFE_INTEGER = BigDecimal.valueOf(LongSerializer.JS_MAX_SAFE_INTEGER);
		public static final BigDecimal JS_MIN_SAFE_INTEGER = BigDecimal.valueOf(LongSerializer.JS_MIN_SAFE_INTEGER);

		public BigDecimalSerializer() {
			super(BigDecimal.class);
		}

		@Override
		public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (Objects.isNull(value)) {
				gen.writeNull();
				return;
			}
			if (isSafeInteger(value)) {
				gen.writeNumber(value);
			} else {
				gen.writeString(value.toPlainString());
			}
		}
	}

	public static class FloatSerializer extends StdSerializer<Float> {
		public FloatSerializer() {
			super(Float.class);
		}

		@Override
		public void serialize(Float value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (Objects.isNull(value)) {
				gen.writeNull();
				return;
			}
			if (!Float.isFinite(value)) {
				gen.writeString(String.valueOf(value));
				return;
			}
			gen.writeRawValue(new BigDecimal(Float.toString(value)).toPlainString());
		}
	}

	public static class DoubleSerializer extends StdSerializer<Double> {
		public DoubleSerializer() {
			super(Double.class);
		}

		@Override
		public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (Objects.isNull(value)) {
				gen.writeNull();
				return;
			}
			if (!Double.isFinite(value)) {
				gen.writeString(String.valueOf(value));
				return;
			}
			gen.writeRawValue(BigDecimal.valueOf(value).toPlainString());
		}
	}
}
