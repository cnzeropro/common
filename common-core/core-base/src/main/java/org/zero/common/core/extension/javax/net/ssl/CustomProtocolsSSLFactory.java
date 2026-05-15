package org.zero.common.core.extension.javax.net.ssl;

import org.zero.common.core.util.java.lang.ArrayUtil;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * 支持自定义启用协议列表的 {@link SSLSocketFactory} 包装器。
 *
 * <p>该类只会在底层 socket 支持目标协议时调用 {@link SSLSocket#setEnabledProtocols(String[])}，
 * 避免因为传入运行时不支持的协议名称而抛出 {@link IllegalArgumentException}。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class CustomProtocolsSSLFactory extends SSLSocketFactory {
	protected final String[] protocols;
	protected final SSLSocketFactory base;

	/**
	 * 构造。
	 *
	 * @param protocols 期望启用的协议列表；为空时保留底层 socket 默认协议配置
	 */
	public CustomProtocolsSSLFactory(String... protocols) {
		this((SSLSocketFactory) SSLSocketFactory.getDefault(), protocols);
	}

	/**
	 * 构造。
	 *
	 * @param sslSocketFactory 原始的 {@link SSLSocketFactory}
	 * @param protocols        期望启用的协议列表；为空时保留底层 socket 默认协议配置
	 */
	public CustomProtocolsSSLFactory(SSLSocketFactory sslSocketFactory, String... protocols) {
		this.base = Objects.requireNonNull(sslSocketFactory, "sslSocketFactory cannot be null");
		this.protocols = Objects.isNull(protocols) ? null : protocols.clone();
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return base.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return base.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket() throws IOException {
		Socket socket = base.createSocket();
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		Socket socket = base.createSocket(s, host, port, autoClose);
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(Socket s, InputStream consumed, boolean autoClose) throws IOException {
		Socket socket = base.createSocket(s, consumed, autoClose);
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		Socket socket = base.createSocket(host, port);
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		Socket socket = base.createSocket(host, port, localHost, localPort);
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		Socket socket = base.createSocket(host, port);
		resetProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		Socket socket = base.createSocket(address, port, localAddress, localPort);
		resetProtocols(socket);
		return socket;
	}

	/**
	 * 重置启用协议列表。
	 *
	 * @param socket socket
	 */
	protected void resetProtocols(Socket socket) {
		if (!(socket instanceof SSLSocket) || !ArrayUtil.nonEmpty(this.protocols)) {
			return;
		}

		String[] enabledProtocols = resolveEnabledProtocols((SSLSocket) socket);
		if (ArrayUtil.nonEmpty(enabledProtocols)) {
			((SSLSocket) socket).setEnabledProtocols(enabledProtocols);
		}
	}

	/**
	 * 解析当前 socket 可安全启用的协议列表。
	 *
	 * @param socket SSL socket
	 * @return 运行时支持的启用协议列表；无匹配协议时返回空数组
	 */
	private String[] resolveEnabledProtocols(SSLSocket socket) {
		return ArrayUtil.intersection(this.protocols, socket.getSupportedProtocols());
	}
}
