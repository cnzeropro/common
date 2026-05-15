package org.zero.common.core.extension.javax.net.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 信任所有证书的 {@link X509ExtendedTrustManager} 实现。
 *
 * <p>该类的所有证书校验方法均为空实现，会接受任意客户端证书和服务端证书，
 * 包括自签名、过期、证书链不完整或不受系统信任的证书。</p>
 *
 * <p>该类不是平台默认 TrustManager，只应在受控测试、内网联调或明确兼容场景中显式使用。
 * 生产环境通常应使用 {@link TrustManagerBuilder} 或平台默认信任链。</p>
 *
 * <p>证书链校验与主机名校验是两件事；如同时使用 {@link TrustAnyHostnameVerifier}，
 * 连接将不再校验证书身份与目标主机是否匹配。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class DefaultTrustManager extends X509ExtendedTrustManager {
	/**
	 * 信任所有证书的共享实例。
	 */
    public static DefaultTrustManager INSTANCE = new DefaultTrustManager();

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
		// 返回空数组表示不限定可接受的颁发者。
        return new X509Certificate[0];
    }
}
