package org.zero.common.core.extension.javax.net.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class CustomProtocolsSSLFactoryTest {
	@Test
	void createSocketShouldEnableOnlySupportedProtocols() throws Exception {
		FakeSSLSocketFactory base = new FakeSSLSocketFactory(
				new String[]{SslProtocols.TLSv13, SslProtocols.TLSv12},
				new String[]{SslProtocols.TLSv12}
		);
		CustomProtocolsSSLFactory factory = new CustomProtocolsSSLFactory(
				base,
				SslProtocols.TLSv13,
				SslProtocols.SSLv3,
				SslProtocols.TLSv13,
				SslProtocols.TLSv12
		);

		FakeSSLSocket socket = (FakeSSLSocket) factory.createSocket();

		assertTrue(socket.protocolsSet);
		assertArrayEquals(new String[]{SslProtocols.TLSv13, SslProtocols.TLSv12}, socket.enabledProtocols);
	}

	@Test
	void createSocketShouldKeepDefaultProtocolsWhenNoRequestedProtocolIsSupported() throws Exception {
		FakeSSLSocketFactory base = new FakeSSLSocketFactory(
				new String[]{SslProtocols.TLSv12},
				new String[]{SslProtocols.TLSv12}
		);
		CustomProtocolsSSLFactory factory = new CustomProtocolsSSLFactory(base, SslProtocols.SSLv3);

		FakeSSLSocket socket = (FakeSSLSocket) factory.createSocket();

		assertFalse(socket.protocolsSet);
		assertArrayEquals(new String[]{SslProtocols.TLSv12}, socket.enabledProtocols);
	}

	@Test
	void constructorShouldCopyProtocols() throws Exception {
		String[] protocols = {SslProtocols.TLSv12};
		FakeSSLSocketFactory base = new FakeSSLSocketFactory(
				new String[]{SslProtocols.TLSv12, SslProtocols.SSLv3},
				new String[]{SslProtocols.TLSv12}
		);
		CustomProtocolsSSLFactory factory = new CustomProtocolsSSLFactory(base, protocols);

		protocols[0] = SslProtocols.SSLv3;
		FakeSSLSocket socket = (FakeSSLSocket) factory.createSocket();

		assertArrayEquals(new String[]{SslProtocols.TLSv12}, socket.enabledProtocols);
	}

	@Test
	void constructorShouldRejectNullBaseFactory() {
		NullPointerException exception = assertThrows(
				NullPointerException.class,
				() -> new CustomProtocolsSSLFactory((SSLSocketFactory) null, SslProtocols.TLSv12)
		);

		assertTrue(exception.getMessage().contains("sslSocketFactory"));
	}

	@Test
	void createSocketWithConsumedInputStreamShouldDelegateAndResetProtocols() throws Exception {
		FakeSSLSocketFactory base = new FakeSSLSocketFactory(
				new String[]{SslProtocols.TLSv12},
				new String[]{SslProtocols.TLSv1}
		);
		CustomProtocolsSSLFactory factory = new CustomProtocolsSSLFactory(base, SslProtocols.TLSv12);

		FakeSSLSocket socket = (FakeSSLSocket) factory.createSocket(
				new Socket(),
				new ByteArrayInputStream(new byte[0]),
				true
		);

		assertTrue(base.inputStreamOverloadCalled);
		assertArrayEquals(new String[]{SslProtocols.TLSv12}, socket.enabledProtocols);
	}

	private static class FakeSSLSocketFactory extends SSLSocketFactory {
		private final String[] supportedProtocols;
		private final String[] enabledProtocols;
		private boolean inputStreamOverloadCalled;

		private FakeSSLSocketFactory(String[] supportedProtocols, String[] enabledProtocols) {
			this.supportedProtocols = supportedProtocols;
			this.enabledProtocols = enabledProtocols;
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return new String[0];
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return new String[0];
		}

		@Override
		public Socket createSocket(Socket s, String host, int port, boolean autoClose) {
			return createSocket();
		}

		@Override
		public Socket createSocket(Socket s, InputStream consumed, boolean autoClose) {
			this.inputStreamOverloadCalled = true;
			return createSocket();
		}

		@Override
		public Socket createSocket(String host, int port) {
			return createSocket();
		}

		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort) {
			return createSocket();
		}

		@Override
		public Socket createSocket(InetAddress host, int port) {
			return createSocket();
		}

		@Override
		public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) {
			return createSocket();
		}

		@Override
		public Socket createSocket() {
			return new FakeSSLSocket(supportedProtocols, enabledProtocols);
		}
	}

	private static class FakeSSLSocket extends SSLSocket {
		private final String[] supportedProtocols;
		private String[] enabledProtocols;
		private boolean protocolsSet;

		private FakeSSLSocket(String[] supportedProtocols, String[] enabledProtocols) {
			this.supportedProtocols = supportedProtocols;
			this.enabledProtocols = enabledProtocols;
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return new String[0];
		}

		@Override
		public String[] getEnabledCipherSuites() {
			return new String[0];
		}

		@Override
		public void setEnabledCipherSuites(String[] suites) {
		}

		@Override
		public String[] getSupportedProtocols() {
			return supportedProtocols;
		}

		@Override
		public String[] getEnabledProtocols() {
			return enabledProtocols;
		}

		@Override
		public void setEnabledProtocols(String[] protocols) {
			this.protocolsSet = true;
			this.enabledProtocols = protocols;
		}

		@Override
		public SSLSession getSession() {
			return null;
		}

		@Override
		public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
		}

		@Override
		public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
		}

		@Override
		public void startHandshake() throws IOException {
		}

		@Override
		public boolean getUseClientMode() {
			return true;
		}

		@Override
		public void setUseClientMode(boolean mode) {
		}

		@Override
		public boolean getNeedClientAuth() {
			return false;
		}

		@Override
		public void setNeedClientAuth(boolean need) {
		}

		@Override
		public boolean getWantClientAuth() {
			return false;
		}

		@Override
		public void setWantClientAuth(boolean want) {
		}

		@Override
		public boolean getEnableSessionCreation() {
			return true;
		}

		@Override
		public void setEnableSessionCreation(boolean flag) {
		}
	}
}
