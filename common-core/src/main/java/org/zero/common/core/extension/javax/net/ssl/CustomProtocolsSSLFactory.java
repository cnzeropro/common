package org.zero.common.core.extension.javax.net.ssl;

import org.zero.common.core.util.java.lang.ArrayUtil;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class CustomProtocolsSSLFactory extends SSLSocketFactory {
    protected final String[] protocols;
    protected final SSLSocketFactory base;

    /**
     * 构造
     *
     * @param protocols 支持协议列表
     */
    public CustomProtocolsSSLFactory(String... protocols) {
        this((SSLSocketFactory) SSLSocketFactory.getDefault(), protocols);
    }

    /**
     * 构造
     *
     * @param sslSocketFactory 原始的 {@link SSLSocketFactory}
     * @param protocols        支持协议列表
     */
    public CustomProtocolsSSLFactory(SSLSocketFactory sslSocketFactory, String... protocols) {
        this.protocols = protocols;
        this.base = sslSocketFactory;
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
     * 重置可用策略
     *
     * @param socket SSLSocket
     */
    private void resetProtocols(Socket socket) {
        if (socket instanceof SSLSocket && ArrayUtil.nonEmpty(this.protocols)) {
            ((SSLSocket) socket).setEnabledProtocols(this.protocols);
        }
    }
}