package org.zero.common.core.extension.java.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.ConstantPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link Properties} 增强读取器。
 * <p>
 * 该类保留 {@link Properties} 的常用读写与加载能力，并在其上补充类型化读取方法。
 * 所有类型化读取都以字符串属性值为来源，属性不存在时返回 {@link Optional#empty()}、{@code null}
 * 或调用方传入的默认值；属性存在但格式不合法时保持 JDK 解析方法的原始异常行为。
 * <p>
 * 文件加载会根据文件名后缀自动选择普通 properties 或 XML properties 格式。
 * <p>
 * 类型化读取方法统一以字符串属性值为来源：
 * <ul>
 *     <li>{@code getXxxOpt} 在属性缺失时返回 {@link Optional#empty()}，属性存在时按目标类型解析；</li>
 *     <li>{@code getBoxedXxx} 返回包装类型，属性缺失时可返回 {@code null}；</li>
 *     <li>{@code getXxx(key)} 返回对象值、基础类型零值或 {@code null}；</li>
 *     <li>{@code getXxxOrDefault(key, defaultValue)} 在属性缺失时使用默认值；</li>
 *     <li>{@code getXxxOrDefault(key, defaultSupplier)} 在属性缺失时调用默认值供应器；</li>
 *     <li>{@code getRequiredXxx} 要求属性存在并能成功解析，否则抛出异常。</li>
 * </ul>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/4/12
 */
@Getter
public class PropertiesEnhancer {
	private static final String XML_FILE_SUFFIX = ".xml";

	/**
	 * 仅代理 {@link IncludedDelegates} 中声明的安全常用方法，避免暴露完整 {@link Properties} 可变接口。
	 * <p>
	 * 返回的是原始引用，调用方修改它会影响当前读取器。
	 */
	@Delegate(types = IncludedDelegates.class)
	private final Properties properties;

	private PropertiesEnhancer(Properties properties) {
		this.properties = Objects.requireNonNull(properties, "properties");
	}

	/**
	 * 创建一个空的增强读取器。
	 *
	 * @return 空 {@link Properties} 对应的增强读取器
	 */
	public static PropertiesEnhancer init() {
		return init(new Properties());
	}

	/**
	 * 使用已有 {@link Properties} 创建增强读取器。
	 * <p>
	 * 该方法不复制传入对象，后续对原始 {@link Properties} 的修改会被当前读取器感知。
	 *
	 * @param properties 属性集合
	 * @return 增强读取器
	 * @throws NullPointerException 当 {@code properties} 为 {@code null} 时抛出
	 */
	public static PropertiesEnhancer init(Properties properties) {
		return new PropertiesEnhancer(properties);
	}

	/**
	 * 从文件路径加载属性。
	 *
	 * @param filePath properties 或 XML properties 文件路径
	 * @return 增强读取器
	 * @throws NullPointerException 当 {@code filePath} 为 {@code null} 时抛出
	 */
	@SneakyThrows
	public static PropertiesEnhancer init(String filePath) {
		return init(Paths.get(Objects.requireNonNull(filePath, "filePath")));
	}

	/**
	 * 从文件路径加载属性。
	 * <p>
	 * 文件名以 {@code .xml} 结尾时使用 {@link Properties#loadFromXML(InputStream)}，
	 * 否则使用 {@link Properties#load(InputStream)}。后缀判断忽略大小写。
	 *
	 * @param filePath properties 或 XML properties 文件路径
	 * @return 增强读取器
	 * @throws NullPointerException 当 {@code filePath} 为 {@code null} 时抛出
	 */
	@SneakyThrows
	public static PropertiesEnhancer init(Path filePath) {
		Objects.requireNonNull(filePath, "filePath");
		Properties properties = new Properties();
		try (InputStream inputStream = Files.newInputStream(filePath)) {
			if (isXml(filePath)) {
				properties.loadFromXML(inputStream);
			} else {
				properties.load(inputStream);
			}
		}
		return init(properties);
	}

	private static boolean isXml(Path filePath) {
		return StringUtil.endWith(filePath.toString(), XML_FILE_SUFFIX, true);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T> Class<T> narrowClass(Class<?> clazz) {
		return (Class) clazz;
	}

	private static NoSuchElementException noProperty(String key, String typeName) {
		return new NoSuchElementException("No property present for key: " + key + " with type: " + typeName);
	}

	private static <T> T requiredProperty(String key, String typeName) {
		throw noProperty(key, typeName);
	}

	/**
	 * 判断属性集合是否非空。
	 *
	 * @return 非空时返回 {@code true}
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	/**
	 * 读取字符串属性。
	 *
	 * @param key 键
	 * @return 属性存在时返回包含值的 {@link Optional}，否则返回空
	 */
	public Optional<String> getStringOpt(String key) {
		return Optional.ofNullable(this.getProperty(key));
	}

	/**
	 * 读取字符串属性。
	 *
	 * @param key 键
	 * @return 属性值，不存在时返回 {@code null}
	 */
	public String getString(String key) {
		return this.getStringOpt(key).orElse(null);
	}

	/**
	 * 读取字符串属性，不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 属性值或默认值
	 */
	public String getStringOrDefault(String key, String defaultValue) {
		return this.getStringOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取字符串属性，不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return 属性值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public String getStringOrDefault(String key, Supplier<String> defaultSupplier) {
		return this.getStringOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填字符串属性。
	 *
	 * @param key 键
	 * @return 属性值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public String getRequiredString(String key) {
		return this.getStringOpt(key).orElseThrow(() -> noProperty(key, String.class.getName()));
	}

	/**
	 * 读取 byte 属性。
	 * <p>
	 * 属性存在但无法解析为 byte 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Byte> getByteOpt(String key) {
		return this.getStringOpt(key).map(Byte::parseByte);
	}

	/**
	 * 读取 byte 包装属性。
	 *
	 * @param key 键
	 * @return byte 包装值，属性不存在时返回 {@code null}
	 */
	public Byte getBoxedByte(String key) {
		return this.getByteOpt(key).orElse(null);
	}

	/**
	 * 读取 byte 属性。
	 *
	 * @param key 键
	 * @return byte 值，属性不存在时返回 {@link ConstantPool#BYTE_ZERO}
	 */
	public byte getByte(String key) {
		return this.getByteOrDefault(key, ConstantPool.BYTE_ZERO);
	}

	/**
	 * 读取 byte 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return byte 值或默认值
	 */
	public byte getByteOrDefault(String key, byte defaultValue) {
		return this.getByteOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 byte 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return byte 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public byte getByteOrDefault(String key, Supplier<Byte> defaultSupplier) {
		return this.getByteOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 byte 属性。
	 *
	 * @param key 键
	 * @return byte 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public byte getRequiredByte(String key) {
		return this.getByteOpt(key).orElseThrow(() -> noProperty(key, Byte.class.getName()));
	}

	/**
	 * 读取 short 属性。
	 * <p>
	 * 属性存在但无法解析为 short 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Short> getShortOpt(String key) {
		return this.getStringOpt(key).map(Short::parseShort);
	}

	/**
	 * 读取 short 包装属性。
	 *
	 * @param key 键
	 * @return short 包装值，属性不存在时返回 {@code null}
	 */
	public Short getBoxedShort(String key) {
		return this.getShortOpt(key).orElse(null);
	}

	/**
	 * 读取 short 属性。
	 *
	 * @param key 键
	 * @return short 值，属性不存在时返回 {@link ConstantPool#SHORT_ZERO}
	 */
	public short getShort(String key) {
		return this.getShortOpt(key).orElse(ConstantPool.SHORT_ZERO);
	}

	/**
	 * 读取 short 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return short 值或默认值
	 */
	public short getShortOrDefault(String key, short defaultValue) {
		return this.getShortOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 short 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return short 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public short getShortOrDefault(String key, Supplier<Short> defaultSupplier) {
		return this.getShortOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 short 属性。
	 *
	 * @param key 键
	 * @return short 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public short getRequiredShort(String key) {
		return this.getShortOpt(key).orElseThrow(() -> noProperty(key, Short.class.getName()));
	}

	/**
	 * 读取 int 属性。
	 * <p>
	 * 属性存在但无法解析为 int 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Integer> getIntOpt(String key) {
		return this.getStringOpt(key).map(Integer::parseInt);
	}

	/**
	 * 读取 int 包装属性。
	 *
	 * @param key 键
	 * @return int 包装值，属性不存在时返回 {@code null}
	 */
	public Integer getBoxedInt(String key) {
		return this.getIntOpt(key).orElse(null);
	}

	/**
	 * 读取 int 属性。
	 *
	 * @param key 键
	 * @return int 值，属性不存在时返回 {@link ConstantPool#INT_ZERO}
	 */
	public int getInt(String key) {
		return this.getIntOpt(key).orElse(ConstantPool.INT_ZERO);
	}

	/**
	 * 读取 int 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return int 值或默认值
	 */
	public int getIntOrDefault(String key, int defaultValue) {
		return this.getIntOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 int 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return int 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public int getIntOrDefault(String key, Supplier<Integer> defaultSupplier) {
		return this.getIntOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 int 属性。
	 *
	 * @param key 键
	 * @return int 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public int getRequiredInt(String key) {
		return this.getIntOpt(key).orElseThrow(() -> noProperty(key, Integer.class.getName()));
	}

	/**
	 * 读取 long 属性。
	 * <p>
	 * 属性存在但无法解析为 long 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Long> getLongOpt(String key) {
		return this.getStringOpt(key).map(Long::parseLong);
	}

	/**
	 * 读取 long 包装属性。
	 *
	 * @param key 键
	 * @return long 包装值，属性不存在时返回 {@code null}
	 */
	public Long getBoxedLong(String key) {
		return this.getLongOpt(key).orElse(null);
	}

	/**
	 * 读取 long 属性。
	 *
	 * @param key 键
	 * @return long 值，属性不存在时返回 {@link ConstantPool#LONG_ZERO}
	 */
	public long getLong(String key) {
		return this.getLongOpt(key).orElse(ConstantPool.LONG_ZERO);
	}

	/**
	 * 读取 long 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return long 值或默认值
	 */
	public long getLongOrDefault(String key, long defaultValue) {
		return this.getLongOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 long 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return long 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public long getLongOrDefault(String key, Supplier<Long> defaultSupplier) {
		return this.getLongOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 long 属性。
	 *
	 * @param key 键
	 * @return long 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public long getRequiredLong(String key) {
		return this.getLongOpt(key).orElseThrow(() -> noProperty(key, Long.class.getName()));
	}

	/**
	 * 读取 float 属性。
	 * <p>
	 * 属性存在但无法解析为 float 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Float> getFloatOpt(String key) {
		return this.getStringOpt(key).map(Float::parseFloat);
	}

	/**
	 * 读取 float 包装属性。
	 *
	 * @param key 键
	 * @return float 包装值，属性不存在时返回 {@code null}
	 */
	public Float getBoxedFloat(String key) {
		return this.getFloatOpt(key).orElse(null);
	}

	/**
	 * 读取 float 属性。
	 *
	 * @param key 键
	 * @return float 值，属性不存在时返回 {@link ConstantPool#FLOAT_ZERO}
	 */
	public float getFloat(String key) {
		return this.getFloatOpt(key).orElse(ConstantPool.FLOAT_ZERO);
	}

	/**
	 * 读取 float 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return float 值或默认值
	 */
	public float getFloatOrDefault(String key, float defaultValue) {
		return this.getFloatOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 float 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return float 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public float getFloatOrDefault(String key, Supplier<Float> defaultSupplier) {
		return this.getFloatOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 float 属性。
	 *
	 * @param key 键
	 * @return float 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public float getRequiredFloat(String key) {
		return this.getFloatOpt(key).orElseThrow(() -> noProperty(key, Float.class.getName()));
	}

	/**
	 * 读取 double 属性。
	 * <p>
	 * 属性存在但无法解析为 double 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Double> getDoubleOpt(String key) {
		return this.getStringOpt(key).map(Double::parseDouble);
	}

	/**
	 * 读取 double 包装属性。
	 *
	 * @param key 键
	 * @return double 包装值，属性不存在时返回 {@code null}
	 */
	public Double getBoxedDouble(String key) {
		return this.getDoubleOpt(key).orElse(null);
	}

	/**
	 * 读取 double 属性。
	 *
	 * @param key 键
	 * @return double 值，属性不存在时返回 {@link ConstantPool#DOUBLE_ZERO}
	 */
	public double getDouble(String key) {
		return this.getDoubleOpt(key).orElse(ConstantPool.DOUBLE_ZERO);
	}

	/**
	 * 读取 double 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return double 值或默认值
	 */
	public double getDoubleOrDefault(String key, double defaultValue) {
		return this.getDoubleOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 double 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return double 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public double getDoubleOrDefault(String key, Supplier<Double> defaultSupplier) {
		return this.getDoubleOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 double 属性。
	 *
	 * @param key 键
	 * @return double 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public double getRequiredDouble(String key) {
		return this.getDoubleOpt(key).orElseThrow(() -> noProperty(key, Double.class.getName()));
	}

	/**
	 * 读取 char 属性。
	 * <p>
	 * 属性存在且非空时取第一个字符；属性不存在或为空字符串时返回空。
	 *
	 * @param key 键
	 * @return char 值
	 */
	public Optional<Character> getCharOpt(String key) {
		return this.getStringOpt(key)
				.filter(value -> !value.isEmpty())
				.map(value -> value.charAt(0));
	}

	/**
	 * 读取 char 包装属性。
	 *
	 * @param key 键
	 * @return char 包装值，属性不存在或为空字符串时返回 {@code null}
	 */
	public Character getBoxedChar(String key) {
		return this.getCharOpt(key).orElse(null);
	}

	/**
	 * 读取 char 属性。
	 *
	 * @param key 键
	 * @return char 值，属性不存在或为空字符串时返回 {@link ConstantPool#CHAR_ZERO}
	 */
	public char getChar(String key) {
		return this.getCharOpt(key).orElse(ConstantPool.CHAR_ZERO);
	}

	/**
	 * 读取 char 属性，属性不存在或为空字符串时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return char 值或默认值
	 */
	public char getCharOrDefault(String key, char defaultValue) {
		return this.getCharOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 char 属性，属性不存在或为空字符串时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return char 值或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public char getCharOrDefault(String key, Supplier<Character> defaultSupplier) {
		return this.getCharOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 char 属性。
	 *
	 * @param key 键
	 * @return char 值
	 * @throws NoSuchElementException 当属性不存在或为空字符串时抛出
	 */
	public char getRequiredChar(String key) {
		return this.getCharOpt(key).orElseThrow(() -> noProperty(key, Character.class.getName()));
	}

	/**
	 * 读取 boolean 属性。
	 * <p>
	 * 使用 {@link Boolean#parseBoolean(String)} 解析，仅忽略大小写等于 {@code true} 时返回 {@code true}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<Boolean> getBooleanOpt(String key) {
		return this.getStringOpt(key).map(Boolean::parseBoolean);
	}

	/**
	 * 读取 boolean 包装属性。
	 *
	 * @param key 键
	 * @return boolean 包装值，属性不存在时返回 {@code null}
	 */
	public Boolean getBoxedBoolean(String key) {
		return this.getBooleanOpt(key).orElse(null);
	}

	/**
	 * 读取 boolean 属性。
	 *
	 * @param key 键
	 * @return boolean 值，属性不存在时返回 {@link ConstantPool#BOOLEAN_FALSE}
	 */
	public boolean getBoolean(String key) {
		return this.getBooleanOpt(key).orElse(ConstantPool.BOOLEAN_FALSE);
	}

	/**
	 * 读取 boolean 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return boolean 值或默认值
	 */
	public boolean getBooleanOrDefault(String key, boolean defaultValue) {
		return this.getBooleanOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 boolean 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return boolean 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public boolean getBooleanOrDefault(String key, Supplier<Boolean> defaultSupplier) {
		return this.getBooleanOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 boolean 属性。
	 *
	 * @param key 键
	 * @return boolean 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public boolean getRequiredBoolean(String key) {
		return this.getBooleanOpt(key).orElseThrow(() -> noProperty(key, Boolean.class.getName()));
	}

	/**
	 * 读取 {@link BigDecimal} 属性。
	 * <p>
	 * 属性存在但无法解析为 {@link BigDecimal} 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<BigDecimal> getBigDecimalOpt(String key) {
		return this.getStringOpt(key).map(BigDecimal::new);
	}

	/**
	 * 读取 {@link BigDecimal} 属性。
	 *
	 * @param key 键
	 * @return BigDecimal 值，属性不存在时返回 {@code null}
	 */
	public BigDecimal getBigDecimal(String key) {
		return this.getBigDecimalOpt(key).orElse(null);
	}

	/**
	 * 读取 {@link BigDecimal} 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return BigDecimal 值或默认值
	 */
	public BigDecimal getBigDecimalOrDefault(String key, BigDecimal defaultValue) {
		return this.getBigDecimalOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 {@link BigDecimal} 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return BigDecimal 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public BigDecimal getBigDecimalOrDefault(String key, Supplier<? extends BigDecimal> defaultSupplier) {
		return this.getBigDecimalOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 {@link BigDecimal} 属性。
	 *
	 * @param key 键
	 * @return BigDecimal 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public BigDecimal getRequiredBigDecimal(String key) {
		return this.getBigDecimalOpt(key).orElseThrow(() -> noProperty(key, BigDecimal.class.getName()));
	}

	/**
	 * 读取 {@link BigInteger} 属性。
	 * <p>
	 * 属性存在但无法解析为 {@link BigInteger} 时抛出 {@link NumberFormatException}。
	 *
	 * @param key 键
	 * @return 解析后的值
	 */
	public Optional<BigInteger> getBigIntegerOpt(String key) {
		return this.getStringOpt(key).map(BigInteger::new);
	}

	/**
	 * 读取 {@link BigInteger} 属性。
	 *
	 * @param key 键
	 * @return BigInteger 值，属性不存在时返回 {@code null}
	 */
	public BigInteger getBigInteger(String key) {
		return this.getBigIntegerOpt(key).orElse(null);
	}

	/**
	 * 读取 {@link BigInteger} 属性，属性不存在时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return BigInteger 值或默认值
	 */
	public BigInteger getBigIntegerOrDefault(String key, BigInteger defaultValue) {
		return this.getBigIntegerOpt(key).orElse(defaultValue);
	}

	/**
	 * 读取 {@link BigInteger} 属性，属性不存在时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @return BigInteger 值或默认值供应器提供的值
	 * @throws NullPointerException 当属性不存在且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public BigInteger getBigIntegerOrDefault(String key, Supplier<? extends BigInteger> defaultSupplier) {
		return this.getBigIntegerOpt(key).orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填 {@link BigInteger} 属性。
	 *
	 * @param key 键
	 * @return BigInteger 值
	 * @throws NoSuchElementException 当属性不存在时抛出
	 */
	public BigInteger getRequiredBigInteger(String key) {
		return this.getBigIntegerOpt(key).orElseThrow(() -> noProperty(key, BigInteger.class.getName()));
	}

	/**
	 * 读取类名并加载为 {@link Class}。
	 * <p>
	 * 类名不存在或加载失败时返回空；类加载细节复用 {@link ClassLoaderUtil#loadClassOpt(CharSequence)}。
	 *
	 * @param key 键
	 * @return 加载成功的类
	 */
	public Optional<Class<?>> getClassOpt(String key) {
		return this.getStringOpt(key).flatMap(ClassLoaderUtil::loadClassOpt);
	}

	/**
	 * 读取类名并加载为 {@link Class}。
	 *
	 * @param key 键
	 * @return 加载成功的类，失败时返回 {@code null}
	 */
	public <T> Class<T> getClass(String key) {
		return this.getClassOpt(key).<Class<T>>map(PropertiesEnhancer::narrowClass).orElse(null);
	}

	/**
	 * 读取类名并加载为 {@link Class}，失败时返回默认值。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 加载成功的类或默认值
	 */
	public <T> Class<T> getClassOrDefault(String key, Class<T> defaultValue) {
		return this.getClassOpt(key).<Class<T>>map(PropertiesEnhancer::narrowClass).orElse(defaultValue);
	}

	/**
	 * 读取类名并加载为 {@link Class}，失败时延迟获取默认值。
	 *
	 * @param key             键
	 * @param defaultSupplier 默认值供应器
	 * @param <T>             Class 指向的目标类型
	 * @return 加载成功的类或默认值供应器提供的值
	 * @throws NullPointerException 当需要默认值且 {@code defaultSupplier} 为 {@code null} 时抛出
	 */
	public <T> Class<T> getClassOrDefault(String key, Supplier<Class<T>> defaultSupplier) {
		return this.getClassOpt(key)
				.<Class<T>>map(PropertiesEnhancer::narrowClass)
				.orElseGet(() -> Objects.requireNonNull(defaultSupplier, "defaultSupplier").get());
	}

	/**
	 * 读取必填类名并加载为 {@link Class}。
	 *
	 * @param key 键
	 * @param <T> Class 指向的目标类型
	 * @return 加载成功的类
	 * @throws NoSuchElementException 当属性不存在或类加载失败时抛出
	 */
	public <T> Class<T> getRequiredClass(String key) {
		return this.getClassOpt(key)
				.<Class<T>>map(PropertiesEnhancer::narrowClass)
				.orElseGet(() -> requiredProperty(key, Class.class.getName()));
	}

	/**
	 * 允许通过 {@link Delegate} 暴露的 {@link Properties} 方法白名单。
	 */
	private interface IncludedDelegates {
		int size();

		boolean isEmpty();

		boolean containsKey(Object key);

		boolean containsValue(Object value);

		void load(Reader reader) throws IOException;

		void load(InputStream inStream) throws IOException;

		void store(Writer writer, String comments) throws IOException;

		void store(OutputStream out, String comments) throws IOException;

		void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException;

		void storeToXML(OutputStream os, String comment) throws IOException;

		void storeToXML(OutputStream os, String comment, String encoding) throws IOException;

		Object setProperty(String key, String value);

		String getProperty(String key);

		String getProperty(String key, String defaultValue);

		Set<String> stringPropertyNames();

		void list(PrintStream out);

		void list(PrintWriter out);
	}
}
