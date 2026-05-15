package org.zero.common.core.extension.javax.net.ssl;

import org.zero.common.core.extension.java.lang.Builder;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;

/**
 * {@link KeyManager} 构建器。
 *
 * <p>{@link KeyManager} 用于 HTTPS 双向认证时向对端提供本端身份信息，例如客户端证书。
 * 默认使用 {@link KeyManagerFactory#getDefaultAlgorithm()} 与 JDK 自动选择的 Provider。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class KeyManagerBuilder implements Builder<KeyManager[], KeyManagerBuilder> {
	/**
	 * KeyManagerFactory 算法名称。
	 */
	protected String algorithm = KeyManagerFactory.getDefaultAlgorithm();
	/**
	 * JCA Provider。
	 *
	 * <p>为空时由 JDK 按算法自动选择默认 Provider。</p>
	 */
	protected Provider provider;
	/**
	 * 私钥与证书所在 KeyStore。
	 *
	 * <p>为空时由底层 {@link KeyManagerFactory#init(KeyStore, char[])} 使用默认配置。</p>
	 */
	protected KeyStore keyStore;
	/**
	 * KeyStore 密码。
	 */
	protected char[] password;

	protected KeyManagerBuilder() {
	}

	public static KeyManagerBuilder create() {
		return new KeyManagerBuilder();
	}

	/**
	 * 设置 KeyManagerFactory 算法。
	 *
	 * @param algorithm 算法名称
	 * @return 当前构建器
	 */
	public KeyManagerBuilder algorithm(String algorithm) {
		this.algorithm = algorithm;
		return this;
	}

	/**
	 * 按 Provider 名称设置 JCA Provider。
	 *
	 * <p>该方法会通过 {@link Security#getProvider(String)} 解析 Provider，并只保存解析后的
	 * {@link Provider} 实例；名称不存在时等同于未指定 Provider。</p>
	 *
	 * @param providerName Provider 名称
	 * @return 当前构建器
	 */
	public KeyManagerBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	/**
	 * 设置 JCA Provider。
	 *
	 * @param provider Provider 实例；为 {@code null} 时使用 JDK 默认选择
	 * @return 当前构建器
	 */
	public KeyManagerBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 设置 KeyStore。
	 *
	 * @param keyStore KeyStore；为 {@code null} 时使用默认配置
	 * @return 当前构建器
	 */
	public KeyManagerBuilder keyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
		return this;
	}

	/**
	 * 使用字符序列设置 KeyStore 密码。
	 *
	 * @param password KeyStore 密码；为 {@code null} 时表示无密码
	 * @return 当前构建器
	 */
	public KeyManagerBuilder password(CharSequence password) {
		if (Objects.isNull(password)) {
			return this.password((char[]) null);
		}
		int length = password.length();
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = password.charAt(i);
		}
		return this.password(chars);
	}

	/**
	 * 使用字符数组设置 KeyStore 密码。
	 *
	 * @param password KeyStore 密码；为 {@code null} 时表示无密码
	 * @return 当前构建器
	 */
	public KeyManagerBuilder password(char[] password) {
		this.password = Objects.isNull(password) ? null : password.clone();
		return this;
	}

	/**
	 * 构建 KeyManager 列表。
	 *
	 * @return KeyManager 列表
	 */
	@Override
	public KeyManager[] build() {
		try {
			KeyManagerFactory factory;
			if (Objects.nonNull(provider)) {
				factory = KeyManagerFactory.getInstance(algorithm, provider);
			} else {
				factory = KeyManagerFactory.getInstance(algorithm);
			}
			factory.init(keyStore, password);
			return factory.getKeyManagers();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Failed to build KeyManagers", e);
		}
	}
}
