package org.zero.common.core.extension.javax.net.ssl;

import javax.net.ssl.SSLSocketFactory;

/**
 * 兼容 Android 低版本 SSL 连接的 SSLSocketFactory。
 *
 * <p>部分低版本 Android 运行时需要显式重置启用协议列表才能完成 TLS 握手。
 * 实际启用时会由父类过滤掉当前运行时不支持的协议。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class AndroidSupportSSLFactory extends CustomProtocolsSSLFactory {
	/**
	 * Android 兼容协议列表。
	 *
	 * <p>强协议排在前面；TLSv1.0 / TLSv1.1 仅作为低版本 Android 兼容补充。</p>
	 */
	protected static final String[] PROTOCOLS = {
			SslProtocols.TLSv13,
			SslProtocols.TLSv12,
			SslProtocols.TLSv11,
			SslProtocols.TLSv1
	};

    public AndroidSupportSSLFactory() {
		this(PROTOCOLS);
	}

	/**
	 * 使用自定义协议列表构造 Android 兼容工厂。
	 *
	 * @param protocols 期望启用的协议列表
	 */
	public AndroidSupportSSLFactory(String... protocols) {
		this(SSLContextBuilder.create().build().getSocketFactory(), protocols);
    }

	/**
	 * 使用自定义底层工厂与协议列表构造 Android 兼容工厂。
	 *
	 * @param sslSocketFactory 原始的 {@link SSLSocketFactory}
	 * @param protocols        期望启用的协议列表
	 */
	public AndroidSupportSSLFactory(SSLSocketFactory sslSocketFactory, String... protocols) {
		super(sslSocketFactory, protocols);
	}
}
