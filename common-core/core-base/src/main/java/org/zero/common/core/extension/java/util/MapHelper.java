package org.zero.common.core.extension.java.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.zero.common.data.constant.ConstantPool;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link Map} 只读辅助器。
 * <p>
 * 该类对外提供面向配置场景的类型化读取方法，不修改底层 {@link Map}。所有类型化读取都只做
 * {@link Class#isInstance(Object)} 判断和安全 cast，不做字符串解析、数字转换或 Bean 转换。
 * <p>
 * 键不存在、值为 {@code null} 或值类型不匹配时，普通 getter 返回 {@code null}、基础类型零值或调用方传入的默认值；
 * {@code getRequired} 系列方法会在上述场景下抛出 {@link NoSuchElementException}。
 * <p>
 * 方法命名约定：
 * <ul>
 *     <li>{@code getXxxOpt} 返回 {@link Optional}，用于显式处理缺失或类型不匹配；</li>
 *     <li>{@code getBoxedXxx} 返回包装类型，缺失或类型不匹配时可返回 {@code null}；</li>
 *     <li>{@code getXxx(key)} 返回对象值、基础类型零值或 {@code null}；</li>
 *     <li>{@code getXxxOrDefault(key, defaultValue)} 在缺失或类型不匹配时使用默认值；</li>
 *     <li>{@code getXxxOrDefault(key, defaultSupplier)} 在缺失或类型不匹配时调用默认值供应器；</li>
 *     <li>{@code getRequiredXxx} 要求值存在且类型匹配，否则抛出异常。</li>
 * </ul>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/3
 */
@Getter
@EqualsAndHashCode
public class MapHelper {
	private static final MapHelper EMPTY = new MapHelper(Collections.emptyMap());

	/**
	 * 返回的是原始引用，调用方修改它会影响当前辅助器。
	 */
	@Delegate(types = IncludedDelegates.class)
	protected final Map<?, ?> map;

	/**
	 * 创建只读辅助器。
	 * <p>
	 * 该构造器不会复制传入 {@link Map}，调用方后续修改底层 {@link Map} 会影响当前辅助器的读取结果。
	 *
	 * @param map 底层 Map
	 * @throws NullPointerException 当 {@code map} 为 {@code null} 时抛出
	 */
	public MapHelper(Map<?, ?> map) {
		this.map = Objects.requireNonNull(map, "map");
	}

	/**
	 * 返回共享的空辅助器。
	 *
	 * @return 空 Map 对应的辅助器
	 */
	public static MapHelper empty() {
		return EMPTY;
	}

	/**
	 * 创建只读辅助器。
	 *
	 * @param map 底层 Map
	 * @return Map 辅助器
	 * @throws NullPointerException 当 {@code map} 为 {@code null} 时抛出
	 */
	public static MapHelper of(Map<?, ?> map) {
		return new MapHelper(map);
	}

	@SuppressWarnings({"rawtypes"})
	private static Class<?> toClass(Object value) {
		return (Class) value;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T> Class<T> narrowClass(Class<?> clazz) {
		return (Class) clazz;
	}

	private static NoSuchElementException noValue(Object key) {
		return new NoSuchElementException("No value present for key: " + key);
	}

	private static NoSuchElementException noValue(Object key, String typeName) {
		return new NoSuchElementException("No value present for key: " + key + " with type: " + typeName);
	}

	private static <T> T requiredValue(Object key, String typeName) {
		throw noValue(key, typeName);
	}

	/**
	 * 判断底层 Map 是否非空。
	 *
	 * @return 非空时返回 {@code true}
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	/**
	 * 读取 {@link Class} 值。
	 * <p>
	 * 运行期仅校验值本身是 {@link Class}，泛型参数由调用方按配置约定保证。
	 *
	 * @param key 键
	 * @return Class 值，不存在或类型不匹配时返回 {@code null}
	 */
	public <T> Class<T> getClass(Object key) {
		return this.getClassOpt(key).<Class<T>>map(MapHelper::narrowClass).orElse(null);
	}

	/**
	 * 读取 {@link Class} 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return Class 值或默认值
	 */
	public <T> Class<T> getClassOrDefault(Object key, Class<T> defaultValue) {
		return this.getClassOpt(key).<Class<T>>map(MapHelper::narrowClass).orElse(defaultValue);
	}

	/**
	 * 读取 {@link Class} 值。
	 *
	 * @param key 键
	 * @return Class 值，不存在或类型不匹配时返回空
	 */
	public Optional<Class<?>> getClassOpt(Object key) {
		return this.getOpt(key)
				.filter(Class.class::isInstance)
				.map(MapHelper::toClass);
	}

	/**
	 * 读取 {@link Class} 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @param <T>             Class 指向的目标类型
	 * @return Class 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public <T> Class<T> getClassOrDefault(Object key, Supplier<Class<T>> defaultSupplier) {
		return this.getClassOpt(key)
				.<Class<T>>map(MapHelper::narrowClass)
				.orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 {@link Class} 值。
	 *
	 * @param key 键
	 * @param <T> Class 指向的目标类型
	 * @return Class 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Class} 时抛出
	 */
	public <T> Class<T> getRequiredClass(Object key) {
		return this.getClassOpt(key)
				.<Class<T>>map(MapHelper::narrowClass)
				.orElseGet(() -> requiredValue(key, Class.class.getName()));
	}

	/**
	 * 读取字符串值。
	 *
	 * @param key 键
	 * @return 字符串值，不存在或类型不匹配时返回空
	 */
	public Optional<String> getStringOpt(Object key) {
		return this.getOpt(key, String.class);
	}

	/**
	 * 读取字符串值。
	 *
	 * @param key 键
	 * @return 字符串值，不存在或类型不匹配时返回 {@code null}
	 */
	public String getString(Object key) {
		return this.getStringOpt(key).orElse(null);
	}

	/**
	 * 读取字符串值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 字符串值或默认值
	 */
	public String getStringOrDefault(Object key, String defaultValue) {
		return this.getStringOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取字符串值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return 字符串值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public String getStringOrDefault(Object key, Supplier<String> defaultSupplier) {
		return this.getStringOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填字符串值。
	 *
	 * @param key 键
	 * @return 字符串值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link String} 时抛出
	 */
	public String getRequiredString(Object key) {
		return this.getStringOpt(key).orElseThrow(() -> noValue(key, String.class.getName()));
	}

	/**
	 * 读取 byte 值。
	 *
	 * @param key 键
	 * @return byte 值，不存在或类型不匹配时返回空
	 */
	public Optional<Byte> getByteOpt(Object key) {
		return this.getOpt(key, Byte.class);
	}

	/**
	 * 读取 byte 包装值。
	 *
	 * @param key 键
	 * @return byte 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Byte getBoxedByte(Object key) {
		return this.getByteOpt(key).orElse(null);
	}

	/**
	 * 读取 byte 值。
	 *
	 * @param key 键
	 * @return byte 值，不存在或类型不匹配时返回 {@link ConstantPool#BYTE_ZERO}
	 */
	public byte getByte(Object key) {
		return this.getByteOrDefault(key, ConstantPool.BYTE_ZERO);
	}

	/**
	 * 读取 byte 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return byte 值或默认值
	 */
	public byte getByteOrDefault(Object key, byte defaultValue) {
		return this.getByteOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 byte 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return byte 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public byte getByteOrDefault(Object key, Supplier<Byte> defaultSupplier) {
		return this.getByteOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 byte 值。
	 *
	 * @param key 键
	 * @return byte 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Byte} 时抛出
	 */
	public byte getRequiredByte(Object key) {
		return this.getByteOpt(key).orElseThrow(() -> noValue(key, Byte.class.getName()));
	}

	/**
	 * 读取 short 值。
	 *
	 * @param key 键
	 * @return short 值，不存在或类型不匹配时返回空
	 */
	public Optional<Short> getShortOpt(Object key) {
		return this.getOpt(key, Short.class);
	}

	/**
	 * 读取 short 包装值。
	 *
	 * @param key 键
	 * @return short 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Short getBoxedShort(Object key) {
		return this.getShortOpt(key).orElse(null);
	}

	/**
	 * 读取 short 值。
	 *
	 * @param key 键
	 * @return short 值，不存在或类型不匹配时返回 {@link ConstantPool#SHORT_ZERO}
	 */
	public short getShort(Object key) {
		return this.getShortOrDefault(key, ConstantPool.SHORT_ZERO);
	}

	/**
	 * 读取 short 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return short 值或默认值
	 */
	public short getShortOrDefault(Object key, short defaultValue) {
		return this.getShortOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 short 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return short 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public short getShortOrDefault(Object key, Supplier<Short> defaultSupplier) {
		return this.getShortOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 short 值。
	 *
	 * @param key 键
	 * @return short 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Short} 时抛出
	 */
	public short getRequiredShort(Object key) {
		return this.getShortOpt(key).orElseThrow(() -> noValue(key, Short.class.getName()));
	}

	/**
	 * 读取 int 值。
	 *
	 * @param key 键
	 * @return int 值，不存在或类型不匹配时返回空
	 */
	public Optional<Integer> getIntOpt(Object key) {
		return this.getOpt(key, Integer.class);
	}

	/**
	 * 读取 int 包装值。
	 *
	 * @param key 键
	 * @return int 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Integer getBoxedInt(Object key) {
		return this.getIntOpt(key).orElse(null);
	}

	/**
	 * 读取 int 值。
	 *
	 * @param key 键
	 * @return int 值，不存在或类型不匹配时返回 {@link ConstantPool#INT_ZERO}
	 */
	public int getInt(Object key) {
		return this.getIntOrDefault(key, ConstantPool.INT_ZERO);
	}

	/**
	 * 读取 int 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return int 值或默认值
	 */
	public int getIntOrDefault(Object key, int defaultValue) {
		return this.getIntOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 int 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return int 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public int getIntOrDefault(Object key, Supplier<Integer> defaultSupplier) {
		return this.getIntOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 int 值。
	 *
	 * @param key 键
	 * @return int 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Integer} 时抛出
	 */
	public int getRequiredInt(Object key) {
		return this.getIntOpt(key).orElseThrow(() -> noValue(key, Integer.class.getName()));
	}

	/**
	 * 读取 long 值。
	 *
	 * @param key 键
	 * @return long 值，不存在或类型不匹配时返回空
	 */
	public Optional<Long> getLongOpt(Object key) {
		return this.getOpt(key, Long.class);
	}

	/**
	 * 读取 long 包装值。
	 *
	 * @param key 键
	 * @return long 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Long getBoxedLong(Object key) {
		return this.getLongOpt(key).orElse(null);
	}

	/**
	 * 读取 long 值。
	 *
	 * @param key 键
	 * @return long 值，不存在或类型不匹配时返回 {@link ConstantPool#LONG_ZERO}
	 */
	public long getLong(Object key) {
		return this.getLongOrDefault(key, ConstantPool.LONG_ZERO);
	}

	/**
	 * 读取 long 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return long 值或默认值
	 */
	public long getLongOrDefault(Object key, long defaultValue) {
		return this.getLongOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 long 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return long 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public long getLongOrDefault(Object key, Supplier<Long> defaultSupplier) {
		return this.getLongOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 long 值。
	 *
	 * @param key 键
	 * @return long 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Long} 时抛出
	 */
	public long getRequiredLong(Object key) {
		return this.getLongOpt(key).orElseThrow(() -> noValue(key, Long.class.getName()));
	}

	/**
	 * 读取 float 值。
	 *
	 * @param key 键
	 * @return float 值，不存在或类型不匹配时返回空
	 */
	public Optional<Float> getFloatOpt(Object key) {
		return this.getOpt(key, Float.class);
	}

	/**
	 * 读取 float 包装值。
	 *
	 * @param key 键
	 * @return float 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Float getBoxedFloat(Object key) {
		return this.getFloatOpt(key).orElse(null);
	}

	/**
	 * 读取 float 值。
	 *
	 * @param key 键
	 * @return float 值，不存在或类型不匹配时返回 {@link ConstantPool#FLOAT_ZERO}
	 */
	public float getFloat(Object key) {
		return this.getFloatOrDefault(key, ConstantPool.FLOAT_ZERO);
	}

	/**
	 * 读取 float 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return float 值或默认值
	 */
	public float getFloatOrDefault(Object key, float defaultValue) {
		return this.getFloatOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 float 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return float 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public float getFloatOrDefault(Object key, Supplier<Float> defaultSupplier) {
		return this.getFloatOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 float 值。
	 *
	 * @param key 键
	 * @return float 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Float} 时抛出
	 */
	public float getRequiredFloat(Object key) {
		return this.getFloatOpt(key).orElseThrow(() -> noValue(key, Float.class.getName()));
	}

	/**
	 * 读取 double 值。
	 *
	 * @param key 键
	 * @return double 值，不存在或类型不匹配时返回空
	 */
	public Optional<Double> getDoubleOpt(Object key) {
		return this.getOpt(key, Double.class);
	}

	/**
	 * 读取 double 包装值。
	 *
	 * @param key 键
	 * @return double 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Double getBoxedDouble(Object key) {
		return this.getDoubleOpt(key).orElse(null);
	}

	/**
	 * 读取 double 值。
	 *
	 * @param key 键
	 * @return double 值，不存在或类型不匹配时返回 {@link ConstantPool#DOUBLE_ZERO}
	 */
	public double getDouble(Object key) {
		return this.getDoubleOrDefault(key, ConstantPool.DOUBLE_ZERO);
	}

	/**
	 * 读取 double 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return double 值或默认值
	 */
	public double getDoubleOrDefault(Object key, double defaultValue) {
		return this.getDoubleOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 double 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return double 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public double getDoubleOrDefault(Object key, Supplier<Double> defaultSupplier) {
		return this.getDoubleOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 double 值。
	 *
	 * @param key 键
	 * @return double 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Double} 时抛出
	 */
	public double getRequiredDouble(Object key) {
		return this.getDoubleOpt(key).orElseThrow(() -> noValue(key, Double.class.getName()));
	}

	/**
	 * 读取 char 值。
	 *
	 * @param key 键
	 * @return char 值，不存在或类型不匹配时返回空
	 */
	public Optional<Character> getCharOpt(Object key) {
		return this.getOpt(key, Character.class);
	}

	/**
	 * 读取 char 包装值。
	 *
	 * @param key 键
	 * @return char 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Character getBoxedChar(Object key) {
		return this.getCharOpt(key).orElse(null);
	}

	/**
	 * 读取 char 值。
	 *
	 * @param key 键
	 * @return char 值，不存在或类型不匹配时返回 {@link ConstantPool#CHAR_ZERO}
	 */
	public char getChar(Object key) {
		return this.getCharOrDefault(key, ConstantPool.CHAR_ZERO);
	}

	/**
	 * 读取 char 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return char 值或默认值
	 */
	public char getCharOrDefault(Object key, char defaultValue) {
		return this.getCharOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 char 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return char 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public char getCharOrDefault(Object key, Supplier<Character> defaultSupplier) {
		return this.getCharOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 char 值。
	 *
	 * @param key 键
	 * @return char 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Character} 时抛出
	 */
	public char getRequiredChar(Object key) {
		return this.getCharOpt(key).orElseThrow(() -> noValue(key, Character.class.getName()));
	}

	/**
	 * 读取 boolean 值。
	 *
	 * @param key 键
	 * @return boolean 值，不存在或类型不匹配时返回空
	 */
	public Optional<Boolean> getBooleanOpt(Object key) {
		return this.getOpt(key, Boolean.class);
	}

	/**
	 * 读取 boolean 包装值。
	 *
	 * @param key 键
	 * @return boolean 包装值，不存在或类型不匹配时返回 {@code null}
	 */
	public Boolean getBoxedBoolean(Object key) {
		return this.getBooleanOpt(key).orElse(null);
	}

	/**
	 * 读取 boolean 值。
	 *
	 * @param key 键
	 * @return boolean 值，不存在或类型不匹配时返回 {@link ConstantPool#BOOLEAN_FALSE}
	 */
	public boolean getBoolean(Object key) {
		return this.getBooleanOrDefault(key, ConstantPool.BOOLEAN_FALSE);
	}

	/**
	 * 读取 boolean 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return boolean 值或默认值
	 */
	public boolean getBooleanOrDefault(Object key, boolean defaultValue) {
		return this.getBooleanOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 boolean 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return boolean 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public boolean getBooleanOrDefault(Object key, Supplier<Boolean> defaultSupplier) {
		return this.getBooleanOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 boolean 值。
	 *
	 * @param key 键
	 * @return boolean 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link Boolean} 时抛出
	 */
	public boolean getRequiredBoolean(Object key) {
		return this.getBooleanOpt(key).orElseThrow(() -> noValue(key, Boolean.class.getName()));
	}

	/**
	 * 读取 {@link BigDecimal} 值。
	 *
	 * @param key 键
	 * @return BigDecimal 值，不存在或类型不匹配时返回空
	 */
	public Optional<BigDecimal> getBigDecimalOpt(Object key) {
		return this.getOpt(key, BigDecimal.class);
	}

	/**
	 * 读取 {@link BigDecimal} 值。
	 *
	 * @param key 键
	 * @return BigDecimal 值，不存在或类型不匹配时返回 {@code null}
	 */
	public BigDecimal getBigDecimal(Object key) {
		return this.getBigDecimalOpt(key).orElse(null);
	}

	/**
	 * 读取 {@link BigDecimal} 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return BigDecimal 值或默认值
	 */
	public BigDecimal getBigDecimalOrDefault(Object key, BigDecimal defaultValue) {
		return this.getBigDecimalOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 {@link BigDecimal} 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return BigDecimal 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public BigDecimal getBigDecimalOrDefault(Object key, Supplier<? extends BigDecimal> defaultSupplier) {
		return this.getBigDecimalOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 {@link BigDecimal} 值。
	 *
	 * @param key 键
	 * @return BigDecimal 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link BigDecimal} 时抛出
	 */
	public BigDecimal getRequiredBigDecimal(Object key) {
		return this.getBigDecimalOpt(key).orElseThrow(() -> noValue(key, BigDecimal.class.getName()));
	}

	/**
	 * 读取 {@link BigInteger} 值。
	 *
	 * @param key 键
	 * @return BigInteger 值，不存在或类型不匹配时返回空
	 */
	public Optional<BigInteger> getBigIntegerOpt(Object key) {
		return this.getOpt(key, BigInteger.class);
	}

	/**
	 * 读取 {@link BigInteger} 值。
	 *
	 * @param key 键
	 * @return BigInteger 值，不存在或类型不匹配时返回 {@code null}
	 */
	public BigInteger getBigInteger(Object key) {
		return this.getBigIntegerOpt(key).orElse(null);
	}

	/**
	 * 读取 {@link BigInteger} 值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return BigInteger 值或默认值
	 */
	public BigInteger getBigIntegerOrDefault(Object key, BigInteger defaultValue) {
		return this.getBigIntegerOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 {@link BigInteger} 值，不存在或类型不匹配时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return BigInteger 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public BigInteger getBigIntegerOrDefault(Object key, Supplier<? extends BigInteger> defaultSupplier) {
		return this.getBigIntegerOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 {@link BigInteger} 值。
	 *
	 * @param key 键
	 * @return BigInteger 值
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或值不是 {@link BigInteger} 时抛出
	 */
	public BigInteger getRequiredBigInteger(Object key) {
		return this.getBigIntegerOpt(key).orElseThrow(() -> noValue(key, BigInteger.class.getName()));
	}

	/**
	 * 按指定类型读取值。
	 *
	 * @param key   键
	 * @param clazz 目标类型
	 * @param <T>   目标值类型
	 * @return 类型匹配的值，不存在或类型不匹配时返回 {@code null}
	 * @throws NullPointerException 当 {@code clazz} 为 {@code null} 时抛出
	 */
	public <T> T get(Object key, Class<T> clazz) {
		return this.getOpt(key, clazz).orElse(null);
	}

	/**
	 * 按指定类型读取值，不存在或类型不匹配时返回默认值。
	 *
	 * @param key          键
	 * @param clazz        目标类型
	 * @param defaultValue 默认值
	 * @param <T>          目标值类型
	 * @return 类型匹配的值或默认值
	 * @throws NullPointerException 当 {@code clazz} 为 {@code null} 时抛出
	 */
	public <T> T getOrDefault(Object key, Class<T> clazz, T defaultValue) {
		return this.getOpt(key, clazz).orElse(defaultValue);
	}

	/**
	 * 读取原始值，不存在时返回默认值。
	 * <p>
	 * 与 {@link Map#getOrDefault(Object, Object)} 一致，键存在且值为 {@code null} 时返回 {@code null}。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 原始值或默认值
	 */
	public Object getOrDefault(Object key, Object defaultValue) {
		Object value = this.get(key);
		return Objects.nonNull(value) || this.containsKey(key) ? value : defaultValue;
	}

	/**
	 * 读取必填原始值。
	 *
	 * @param key 键
	 * @return 原始值
	 * @throws NoSuchElementException 当键不存在或值为 {@code null} 时抛出
	 */
	public Object getRequired(Object key) {
		return this.getOpt(key)
				.orElseThrow(() -> noValue(key));
	}

	/**
	 * 按指定类型读取必填值。
	 *
	 * @param key   键
	 * @param clazz 目标类型
	 * @param <T>   目标值类型
	 * @return 类型匹配的值
	 * @throws NullPointerException   当 {@code clazz} 为 {@code null} 时抛出
	 * @throws NoSuchElementException 当键不存在、值为 {@code null} 或类型不匹配时抛出
	 */
	public <T> T getRequired(Object key, Class<T> clazz) {
		Objects.requireNonNull(clazz, "clazz");
		return this.getOpt(key, clazz)
				.orElseThrow(() -> noValue(key, clazz.getName()));
	}

	/**
	 * 读取原始值。
	 *
	 * @param key 键
	 * @return 原始值，不存在或值为 {@code null} 时返回空
	 */
	public Optional<Object> getOpt(Object key) {
		Object value = this.get(key);
		return Optional.ofNullable(value);
	}

	/**
	 * 按指定类型读取值。
	 *
	 * @param key   键
	 * @param clazz 目标类型
	 * @param <T>   目标值类型
	 * @return 类型匹配的值，不存在、值为 {@code null} 或类型不匹配时返回空
	 * @throws NullPointerException 当 {@code clazz} 为 {@code null} 时抛出
	 */
	public <T> Optional<T> getOpt(Object key, Class<T> clazz) {
		Objects.requireNonNull(clazz, "clazz");
		return this.getOpt(key).filter(clazz::isInstance).map(clazz::cast);
	}

	/**
	 * 允许通过 {@link Delegate} 暴露的 {@link Map} 只读方法白名单。
	 */
	private interface IncludedDelegates {
		Object get(Object key);

		int size();

		boolean isEmpty();

		boolean containsKey(Object key);

		boolean containsValue(Object value);

		Set<?> keySet();

		Collection<?> values();

		Set<? extends Map.Entry<?, ?>> entrySet();
	}
}
