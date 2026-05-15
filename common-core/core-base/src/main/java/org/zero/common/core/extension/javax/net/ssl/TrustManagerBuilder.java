package org.zero.common.core.extension.javax.net.ssl;

import org.zero.common.core.extension.java.lang.Builder;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;

/**
 * {@link TrustManager} 构建器。
 *
 * <p>{@link TrustManager} 用于校验对端发送过来的证书，例如客户端校验服务端证书。
 * 默认使用 {@link TrustManagerFactory#getDefaultAlgorithm()} 与 JDK 自动选择的 Provider。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class TrustManagerBuilder implements Builder<TrustManager[], TrustManagerBuilder> {
	/**
	 * TrustManagerFactory 算法名称。
	 */
	protected String algorithm = TrustManagerFactory.getDefaultAlgorithm();
	/**
	 * JCA Provider。
	 *
	 * <p>为空时由 JDK 按算法自动选择默认 Provider。</p>
	 */
	protected Provider provider;
	/**
	 * 可信证书所在 KeyStore。
	 *
	 * <p>为空时由底层 {@link TrustManagerFactory#init(KeyStore)} 使用平台默认证书信任链。</p>
	 */
	protected KeyStore keyStore;

	protected TrustManagerBuilder() {
	}

	public static TrustManagerBuilder create() {
		return new TrustManagerBuilder();
	}

	/**
	 * 设置 TrustManagerFactory 算法。
	 *
	 * @param algorithm 算法名称
	 * @return 当前构建器
	 */
	public TrustManagerBuilder algorithm(String algorithm) {
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
	public TrustManagerBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	/**
	 * 设置 JCA Provider。
	 *
	 * @param provider Provider 实例；为 {@code null} 时使用 JDK 默认选择
	 * @return 当前构建器
	 */
	public TrustManagerBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 设置可信证书 KeyStore。
	 *
	 * @param keyStore KeyStore；为 {@code null} 时使用平台默认证书信任链
	 * @return 当前构建器
	 */
	public TrustManagerBuilder keyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
		return this;
	}

	/**
	 * 构建 TrustManager 列表。
	 *
	 * @return TrustManager 列表
	 */
	@Override
	public TrustManager[] build() {
		try {
			TrustManagerFactory factory;
			if (Objects.nonNull(provider)) {
				factory = TrustManagerFactory.getInstance(algorithm, provider);
			} else {
				factory = TrustManagerFactory.getInstance(algorithm);
			}
			factory.init(keyStore);
			return factory.getTrustManagers();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Failed to build TrustManagers", e);
		}
	}
}
