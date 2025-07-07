package org.zero.common.core.extension.javax.net.ssl;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.BaseBuilder;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.Provider;
import java.util.Objects;

/**
 * {@link TrustManager} 构建器
 * <p>
 * {@link TrustManager}：用于客户端检测服务端发送过来的证书
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class TrustManagerBuilder extends BaseBuilder<TrustManager[], TrustManagerBuilder> {
    protected String algorithm = TrustManagerFactory.getDefaultAlgorithm();
    protected String providerName;
    protected Provider provider;
    protected KeyStore keyStore;

    protected TrustManagerBuilder() {
    }

    public static TrustManagerBuilder create() {
        return new TrustManagerBuilder();
    }

    public TrustManagerBuilder algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public TrustManagerBuilder providerName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public TrustManagerBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public TrustManagerBuilder keyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    @SneakyThrows
    @Override
    protected TrustManager[] instance() {
        TrustManagerFactory factory;
        if (Objects.nonNull(provider)) {
            factory = TrustManagerFactory.getInstance(algorithm, provider);
        } else if (CharSequenceUtil.nonBlank(providerName)) {
            factory = TrustManagerFactory.getInstance(algorithm, providerName);
        } else {
            factory = TrustManagerFactory.getInstance(algorithm);
        }
        factory.init(keyStore);
        return factory.getTrustManagers();
    }
}
