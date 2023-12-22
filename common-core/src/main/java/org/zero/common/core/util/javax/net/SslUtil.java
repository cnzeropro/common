package org.zero.common.core.util.javax.net;

import cn.hutool.core.net.DefaultTrustManager;
import cn.hutool.core.net.SSLProtocols;
import cn.hutool.http.ssl.DefaultSSLInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Objects;

/**
 * @author zero
 * @since 2023/8/10
 */
@Slf4j
@UtilityClass
public class SslUtil {
    public String DEFAULT_PROTOCOL = SSLProtocols.TLS;
    public String DEFAULT_CERTIFICATE_TYPE = "X.509";
    public X509TrustManager DEFAULT_TRUST_MANAGER = DefaultTrustManager.INSTANCE;
    public HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = DefaultSSLInfo.TRUST_ANY_HOSTNAME_VERIFIER;
    public SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY = DefaultSSLInfo.DEFAULT_SSF;
    public TrustManager[] DEFAULT_TRUST_MANAGERS = {DEFAULT_TRUST_MANAGER};

    public SSLSocketFactory createSslSocketFactory(SSLContext sslContext) {
        return sslContext.getSocketFactory();
    }

    public SSLContext createSslContext(KeyManager... keyManagers) throws NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(DEFAULT_PROTOCOL, keyManagers);
    }

    public SSLContext createSslContext(String protocol, KeyManager... keyManagers) throws NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(protocol, keyManagers, DEFAULT_TRUST_MANAGERS);
    }

    public SSLContext createSslContext(KeyManager keyManager, TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(DEFAULT_PROTOCOL, keyManager, trustManager);
    }

    public SSLContext createSslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(DEFAULT_PROTOCOL, keyManagers, trustManagers);
    }

    public SSLContext createSslContext(String protocol, KeyManager keyManager, TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        return createSslContext(protocol,
            Objects.isNull(keyManager) ? null : new KeyManager[]{keyManager},
            Objects.isNull(trustManager) ? null : new TrustManager[]{trustManager});
    }

    public SSLContext createSslContext(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(keyManagers, trustManagers, SecureRandom.getInstanceStrong());
        return sslContext;
    }

    /**
     * 获取 KeyStore。
     * KeyStore：用于管理密钥和证书的存储库，可以用来存储密钥对、证书链、信任根证书等
     */
    public KeyStore createKeyStore(String filepath, char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        return createKeyStore(KeyStore.getDefaultType(), filepath, password);
    }

    /**
     * 获取 KeyStore。
     * KeyStore：用于管理密钥和证书的存储库，可以用来存储密钥对、证书链、信任根证书等
     */
    public KeyStore createKeyStore(String type, String filepath, char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(getInputStream(filepath), password);
        return keyStore;
    }

    /**
     * 使用默认算法获取 KeyManagers。
     * KeyManager：用于HTTPS双向认证时，客户端向服务端发送的认证信息（证书），与不同的服务端交互时，客户端可能用不同的身份（证书）
     */
    public KeyManager[] createKeyManager(KeyStore keyStore, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        return createKeyManager(KeyManagerFactory.getDefaultAlgorithm(), keyStore, password);
    }

    /**
     * 获取 KeyManagers。
     * KeyManager：用于HTTPS双向认证时，客户端向服务端发送的认证信息（证书），与不同的服务端交互时，客户端可能用不同的身份（证书）
     */
    public KeyManager[] createKeyManager(String algorithm, KeyStore keyStore, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
        factory.init(keyStore, password);
        return factory.getKeyManagers();
    }

    /**
     * 使用默认算法获取 TrustManagers。
     * TrustManager：用于客户端检测服务端发送过来的证书
     */
    public TrustManager[] createTrustManager(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
        return createTrustManager(TrustManagerFactory.getDefaultAlgorithm(), keyStore);
    }

    /**
     * 获取 TrustManagers。
     * TrustManager：用于客户端检测服务端发送过来的证书
     */
    public TrustManager[] createTrustManager(String algorithm, KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
        factory.init(keyStore);
        return factory.getTrustManagers();
    }

    /**
     * 获取X.509数字证书
     */
    public Certificate createCertificate(String filepath) throws CertificateException, IOException {
        return createCertificate(DEFAULT_CERTIFICATE_TYPE, filepath);
    }

    /**
     * 获取数字证书
     */
    public Certificate createCertificate(String certificateType, String filepath) throws CertificateException, IOException {
        CertificateFactory factory = CertificateFactory.getInstance(certificateType);
        return factory.generateCertificate(getInputStream(filepath));
    }

    /**
     * 获取文件输入流
     */
    private InputStream getInputStream(String filepath) throws IOException {
        return Files.newInputStream(Paths.get(filepath));
    }
}
