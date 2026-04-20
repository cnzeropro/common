package org.zero.common.core.extension.java.security;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;

/**
 * 用于按不同输入源构建 {@link KeyStore} 的 Builder。
 * <p>
 * 支持通过 {@link KeyStore.LoadStoreParameter}、字节数组、输入流或文件路径加载 KeyStore。
 * 除 {@link #loadStoreParameter} 外，其余输入源互斥，并遵循“最后一次配置生效”的规则。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyStore">KeyStore Types</a>
 * @since 2025/4/30
 */
public class KeyStoreBuilder implements Builder<KeyStore, KeyStoreBuilder> {
	/**
	 * KeyStore 类型。
	 * <p>
	 * 默认使用 {@link KeyStore#getDefaultType()}。
	 */
	protected String type = KeyStore.getDefaultType();
	/**
	 * KeyStore 提供者。
	 * <p>
	 * 未指定时使用 JCA 默认 Provider。
	 */
	protected Provider provider;
	/**
	 * JCA 标准加载参数。
	 * <p>
	 * 设置后会清空 {@link #bytes}、{@link #inputStream} 和 {@link #path}。
	 */
	protected KeyStore.LoadStoreParameter loadStoreParameter;
	/**
	 * KeyStore 字节数组输入源。
	 * <p>
	 * 与 {@link #inputStream}、{@link #path}、{@link #loadStoreParameter} 互斥，最后一次配置生效。
	 */
	protected byte[] bytes;
	/**
	 * KeyStore 输入流输入源。
	 * <p>
	 * 与 {@link #bytes}、{@link #path}、{@link #loadStoreParameter} 互斥，最后一次配置生效。
	 */
	protected InputStream inputStream;
	/**
	 * KeyStore 文件路径输入源。
	 * <p>
	 * 与 {@link #bytes}、{@link #inputStream}、{@link #loadStoreParameter} 互斥，最后一次配置生效。
	 * 在 {@link #build()} 之前仅记录路径，不会立即打开文件流。
	 */
	protected Path path;
	/**
	 * KeyStore 加载密码。
	 * <p>
	 * 使用 {@link #loadStoreParameter} 时可不需要该字段；其余输入源加载时必须提供。
	 */
	protected char[] password;

	protected KeyStoreBuilder() {
	}

	public static KeyStoreBuilder create() {
		return new KeyStoreBuilder();
	}

	public KeyStoreBuilder type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 通过 Provider 名称设置 KeyStore 提供者。
	 *
	 * @param providerName Provider 名称
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	public KeyStoreBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 设置 JCA 标准加载参数。
	 * <p>
	 * 设置后会清空其它原始输入源配置。
	 *
	 * @param loadStoreParameter KeyStore 加载参数
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder loadStoreParameter(KeyStore.LoadStoreParameter loadStoreParameter) {
		this.loadStoreParameter = loadStoreParameter;
		if (Objects.nonNull(loadStoreParameter)) {
			this.bytes = null;
			this.inputStream = null;
			this.path = null;
		}
		return this;
	}

	/**
	 * 使用字节数组作为输入源。
	 *
	 * @param bytes KeyStore 字节数组
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder bytes(byte[] bytes) {
		this.loadStoreParameter = null;
		this.bytes = bytes;
		this.inputStream = null;
		this.path = null;
		return this;
	}

	/**
	 * 使用文件路径作为输入源。
	 *
	 * @param filepath KeyStore 文件路径字符串
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder filepath(String filepath) {
		return this.path(Paths.get(filepath));
	}

	/**
	 * 使用文件路径作为输入源。
	 * <p>
	 * 调用该方法时仅记录路径，不会立即打开文件流；实际加载发生在 {@link #build()}。
	 *
	 * @param path KeyStore 文件路径
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder path(Path path) {
		this.loadStoreParameter = null;
		this.bytes = null;
		this.inputStream = null;
		this.path = path;
		return this;
	}

	/**
	 * 使用输入流作为输入源。
	 *
	 * @param inputStream KeyStore 输入流
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder inputStream(InputStream inputStream) {
		this.loadStoreParameter = null;
		this.bytes = null;
		this.inputStream = inputStream;
		this.path = null;
		return this;
	}

	/**
	 * 设置加载密码。
	 *
	 * @param password 密码字符序列
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder password(CharSequence password) {
		Objects.requireNonNull(password, "password cannot be null");
		int length = password.length();
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = password.charAt(i);
		}
		return this.password(chars);
	}

	/**
	 * 设置加载密码。
	 *
	 * @param password 密码字符数组
	 * @return 当前 Builder
	 */
	public KeyStoreBuilder password(char[] password) {
		this.password = password;
		return this;
	}

	@SneakyThrows
	protected InputStream resolveInputStream() {
		if (Objects.nonNull(bytes)) {
			return new ByteArrayInputStream(bytes);
		}
		if (Objects.nonNull(inputStream)) {
			return inputStream;
		}
		if (Objects.nonNull(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}

	/**
	 * 构建 {@link KeyStore}。
	 * <p>
	 * 优先级如下：
	 * <ol>
	 *     <li>若设置了 {@link #loadStoreParameter}，则按该参数加载</li>
	 *     <li>否则按 {@link #bytes}、{@link #inputStream}、{@link #path} 的最后一次有效配置加载</li>
	 * </ol>
	 *
	 * @return 加载后的 {@link KeyStore}
	 */
	@SneakyThrows
	@Override
	public KeyStore build() {
		KeyStore keyStore = Objects.nonNull(provider) ? KeyStore.getInstance(type, provider) : KeyStore.getInstance(type);
		if (Objects.nonNull(loadStoreParameter)) {
			keyStore.load(loadStoreParameter);
		} else {
			@Cleanup InputStream resolvedInputStream = resolveInputStream();
			Objects.requireNonNull(resolvedInputStream, "inputStream cannot be null");
			Objects.requireNonNull(password, "password cannot be null");
			keyStore.load(resolvedInputStream, password);
		}
		return keyStore;
	}
}
