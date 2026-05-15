package org.zero.common.core.extension.javax.net.ssl;

import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

/**
 * {@link SSLContext} 构建器。
 *
 * <p>默认使用 {@link SslProtocols#TLS} 协议，并沿用 JDK 平台默认的 {@link TrustManager} 与
 * {@link KeyManager}。如需跳过证书校验，必须显式调用 {@link #trustAll()}。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class SSLContextBuilder implements Builder<SSLContext, SSLContextBuilder>, SslProtocols {
	/**
	 * SSLContext 协议名称。
	 *
	 * <p>为空时直接返回 {@link SSLContext#getDefault()}，不再重复初始化默认上下文。</p>
	 */
	protected String protocol = TLS;
	/**
	 * JCA Provider。
	 *
	 * <p>为空时由 JDK 按协议自动选择默认 Provider。</p>
	 */
	protected Provider provider;
	/**
	 * 密钥管理器。
	 *
	 * <p>为空时由底层 {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} 使用默认配置。</p>
	 */
	protected KeyManager[] keyManagers;
	/**
	 * 信任管理器。
	 *
	 * <p>为空时使用平台默认证书信任链。</p>
	 */
	protected TrustManager[] trustManagers;
	/**
	 * 安全随机源。
	 */
	protected SecureRandom secureRandom = new SecureRandom();

	protected SSLContextBuilder() {
	}

	public static SSLContextBuilder create() {
		return new SSLContextBuilder();
	}

	/**
	 * 设置 SSLContext 协议。
	 *
	 * @param protocol 协议名称，例如 {@link SslProtocols#TLS}
	 * @return 当前构建器
	 */
	public SSLContextBuilder protocol(String protocol) {
		this.protocol = protocol;
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
	public SSLContextBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	/**
	 * 设置 JCA Provider。
	 *
	 * @param provider Provider 实例；为 {@code null} 时使用 JDK 默认选择
	 * @return 当前构建器
	 */
	public SSLContextBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 设置密钥管理器。
	 *
	 * @param keyManagers 密钥管理器列表；为 {@code null} 时使用默认配置
	 * @return 当前构建器
	 */
	public SSLContextBuilder keyManagers(KeyManager... keyManagers) {
		this.keyManagers = Objects.isNull(keyManagers) ? null : keyManagers.clone();
		return this;
	}

	/**
	 * 设置信任管理器。
	 *
	 * @param trustManagers 信任管理器列表；为 {@code null} 时使用平台默认证书信任链
	 * @return 当前构建器
	 */
	public SSLContextBuilder trustManagers(TrustManager... trustManagers) {
		this.trustManagers = Objects.isNull(trustManagers) ? null : trustManagers.clone();
		return this;
	}

	/**
	 * 信任所有证书。
	 *
	 * <p>该配置会跳过证书链校验，只应在受控测试或明确兼容场景中显式使用。</p>
	 *
	 * @return 当前构建器
	 */
	public SSLContextBuilder trustAll() {
		this.trustManagers = new TrustManager[]{DefaultTrustManager.INSTANCE};
		return this;
	}

	/**
	 * 设置安全随机源。
	 *
	 * @param secureRandom 安全随机源；为 {@code null} 时由底层 SSLContext 使用默认随机源
	 * @return 当前构建器
	 */
	public SSLContextBuilder secureRandom(SecureRandom secureRandom) {
		this.secureRandom = secureRandom;
		return this;
	}

	/**
	 * 构建 SSLContext。
	 *
	 * @return 初始化后的 SSLContext；协议为空时返回 JDK 默认 SSLContext
	 */
	@Override
	public SSLContext build() {
		try {
			if (CharSequenceUtil.isBlank(protocol)) {
				return SSLContext.getDefault();
			}

			SSLContext sslContext;
			if (Objects.nonNull(provider)) {
				sslContext = SSLContext.getInstance(protocol, provider);
			} else {
				sslContext = SSLContext.getInstance(protocol);
			}
			sslContext.init(keyManagers, trustManagers, secureRandom);
			return sslContext;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Failed to build SSLContext", e);
		}
	}
}
