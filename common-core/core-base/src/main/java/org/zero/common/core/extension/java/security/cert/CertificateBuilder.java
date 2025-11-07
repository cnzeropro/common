package org.zero.common.core.extension.java.security.cert;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.javax.net.ssl.SslUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
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
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#CertificateFactory">CertificateFactory Types</a>
 * @since 2025/4/30
 */
public class CertificateBuilder implements Builder<Certificate[], CertificateBuilder> {
	protected String type = SslUtil.DEFAULT_CERTIFICATE_TYPE;
	protected Provider provider;
	/**
	 * 输入流
	 * <p>
	 * 与 {@link #bytes} 二选一，唯一区别在于使用字节数组可以重复构建，但输入流却可以处理大数据
	 */
	protected InputStream inputStream;
	/**
	 * 字节数组
	 * <p>
	 * 与 {@link #inputStream} 二选一，唯一区别在于使用字节数组可以重复构建，但输入流却可以处理大数据
	 */
	protected byte[] bytes;

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
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	public CertificateBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	public CertificateBuilder bytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}

	public CertificateBuilder filepath(String filepath) {
		return this.path(Paths.get(filepath));
	}

	@SneakyThrows
	public CertificateBuilder path(Path path) {
		return this.inputStream(Files.newInputStream(path));
	}

	public CertificateBuilder inputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	@SneakyThrows
	@Override
	public Certificate[] build() {
		CertificateFactory factory = Objects.nonNull(provider) ? CertificateFactory.getInstance(type, provider) : CertificateFactory.getInstance(type);
		@Cleanup InputStream inputStream = Objects.nonNull(bytes) ? new ByteArrayInputStream(bytes) : this.inputStream;
		Collection<? extends Certificate> certificates = factory.generateCertificates(inputStream);
		return certificates.toArray(new Certificate[0]);
	}
}
