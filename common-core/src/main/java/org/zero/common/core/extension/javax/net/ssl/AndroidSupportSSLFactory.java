package org.zero.common.core.extension.javax.net.ssl;

/**
 * 兼容 Android 低版本 SSL 连接
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class AndroidSupportSSLFactory extends CustomProtocolsSSLFactory {
    // Android 低版本不重置的话某些 SSL 访问就会失败
    protected static final String[] protocols = {SslProtocols.SSLv3, SslProtocols.TLSv1, SslProtocols.TLSv11, SslProtocols.TLSv12};

    public AndroidSupportSSLFactory() {
        super(SSLContextBuilder.create().build().getSocketFactory(), protocols);
    }
}
