package org.zero.common.core.util.javax.net.ssl;

import org.zero.common.core.extension.javax.net.ssl.AndroidSupportSSLFactory;
import org.zero.common.core.extension.javax.net.ssl.CustomProtocolsSSLFactory;
import org.zero.common.core.extension.javax.net.ssl.DefaultTrustManager;
import org.zero.common.core.extension.javax.net.ssl.SslProtocols;
import org.zero.common.core.extension.javax.net.ssl.TrustAnyHostnameVerifier;
import org.zero.common.core.util.java.lang.JdkUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author zero
 * @since 2023/8/10
 */
public class SslUtil {
	public static final String DEFAULT_PROTOCOL = SslProtocols.TLS;
	public static final String DEFAULT_CERTIFICATE_TYPE = "X.509";
	public static final X509TrustManager DEFAULT_TRUST_MANAGER = DefaultTrustManager.INSTANCE;
	public static final HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = TrustAnyHostnameVerifier.INSTANCE;
	public static final SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY;
	public static final TrustManager[] DEFAULT_TRUST_MANAGERS = {DEFAULT_TRUST_MANAGER};

	static {
		if (JdkUtil.IS_ANDROID) {
			// 兼容 android 低版本 SSL 连接
			DEFAULT_SSL_SOCKET_FACTORY = new AndroidSupportSSLFactory();
		} else {
			DEFAULT_SSL_SOCKET_FACTORY = new CustomProtocolsSSLFactory();
		}
	}

	protected SslUtil() {
		throw new UnsupportedOperationException();
	}
}
