package org.zero.common.core.extension.javax.net.ssl;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class SSLContextBuilder implements Builder<SSLContext, SSLContextBuilder>, SslProtocols {
	protected String protocol = TLS;
	protected String providerName;
	protected Provider provider;
	protected KeyManager[] keyManagers;
	protected TrustManager[] trustManagers = {DefaultTrustManager.INSTANCE};
	protected SecureRandom secureRandom = new SecureRandom();

	protected SSLContextBuilder() {
	}

	public static SSLContextBuilder create() {
		return new SSLContextBuilder();
	}

	public SSLContextBuilder protocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public SSLContextBuilder keyManagers(KeyManager... keyManagers) {
		this.keyManagers = keyManagers;
		return this;
	}

	public SSLContextBuilder trustManagers(TrustManager... trustManagers) {
		this.trustManagers = trustManagers;
		return this;
	}

	public SSLContextBuilder secureRandom(SecureRandom secureRandom) {
		this.secureRandom = secureRandom;
		return this;
	}

	@SneakyThrows
	@Override
	public SSLContext build() {
		SSLContext sslContext;
		if (CharSequenceUtil.isBlank(protocol)) {
			sslContext = SSLContext.getDefault();
		} else {
			if (Objects.nonNull(provider)) {
				sslContext = SSLContext.getInstance(protocol, provider);
			} else if (CharSequenceUtil.nonBlank(providerName)) {
				sslContext = SSLContext.getInstance(protocol, providerName);
			} else {
				sslContext = SSLContext.getInstance(protocol);
			}
		}
		sslContext.init(keyManagers, trustManagers, secureRandom);
		return sslContext;
	}
}
