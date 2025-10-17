package org.zero.common.core.extension.java.security.cert;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.javax.net.ssl.SslUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Objects;

/**
 * {@link Certificate} 构建器
 * <p>
 * {@link Certificate}：用于验证实体的身份、分发公钥并确保证书的完整性和合法性
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class CertificateBuilder implements Builder<Certificate[], CertificateBuilder> {
    protected String type = SslUtil.DEFAULT_CERTIFICATE_TYPE;
    protected String providerName;
    protected Provider provider;
    protected InputStream inputStream;

    protected CertificateBuilder() {
    }

    public static CertificateBuilder create() {
        return new CertificateBuilder();
    }

    public CertificateBuilder type(String type) {
        this.type = type;
        return this;
    }

    public CertificateBuilder providerName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public CertificateBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    @SneakyThrows
    public CertificateBuilder filepath(String filepath) {
        return this.inputStream(Files.newInputStream(Paths.get(filepath)));
    }

    public CertificateBuilder inputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    @SneakyThrows
    @Override
    public Certificate[] build() {
        CertificateFactory factory;
        if (Objects.nonNull(provider)) {
            factory = CertificateFactory.getInstance(type, provider);
        } else if (CharSequenceUtil.nonBlank(providerName)) {
            factory = CertificateFactory.getInstance(type, providerName);
        } else {
            factory = CertificateFactory.getInstance(type);
        }
        Collection<? extends Certificate> certificates = factory.generateCertificates(inputStream);
        IoUtil.close(inputStream);
        return certificates.toArray(new Certificate[0]);
    }
}
