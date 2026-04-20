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
 * 用于按不同输入源构建 {@link Certificate} 数组的 Builder。
 * <p>
 * 支持通过字节数组、输入流或文件路径加载证书集合；三种输入源互斥，并遵循“最后一次配置生效”的规则。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#CertificateFactory">CertificateFactory Types</a>
 * @since 2025/4/30
 */
public class CertificateBuilder implements Builder<Certificate[], CertificateBuilder> {
	/**
	 * 证书类型。
	 * <p>
	 * 默认使用 {@link SslUtil#DEFAULT_CERTIFICATE_TYPE}。
	 */
	protected String type = SslUtil.DEFAULT_CERTIFICATE_TYPE;
	/**
	 * 证书工厂提供者。
	 * <p>
	 * 未指定时使用 JCA 默认 Provider。
	 */
	protected Provider provider;
	/**
	 * 输入流输入源。
	 * <p>
	 * 与 {@link #bytes}、{@link #path} 互斥，最后一次配置生效。
	 */
	protected InputStream inputStream;
	/**
	 * 字节数组输入源。
	 * <p>
	 * 与 {@link #inputStream}、{@link #path} 互斥，最后一次配置生效。
	 */
	protected byte[] bytes;
	/**
	 * 证书文件路径输入源。
	 * <p>
	 * 与 {@link #bytes}、{@link #inputStream} 互斥，最后一次配置生效。
	 * 在 {@link #build()} 之前仅记录路径，不会立即打开文件流。
	 */
	protected Path path;

	protected CertificateBuilder() {
	}

	public static CertificateBuilder create() {
		return new CertificateBuilder();
	}

	public CertificateBuilder type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * 通过 Provider 名称设置证书工厂提供者。
	 *
	 * @param providerName Provider 名称
	 * @return 当前 Builder
	 */
	public CertificateBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	public CertificateBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	/**
	 * 使用字节数组作为输入源。
	 *
	 * @param bytes 证书字节数组
	 * @return 当前 Builder
	 */
	public CertificateBuilder bytes(byte[] bytes) {
		this.bytes = bytes;
		this.inputStream = null;
		this.path = null;
		return this;
	}

	/**
	 * 使用文件路径作为输入源。
	 *
	 * @param filepath 证书文件路径字符串
	 * @return 当前 Builder
	 */
	public CertificateBuilder filepath(String filepath) {
		return this.path(Paths.get(filepath));
	}

	/**
	 * 使用文件路径作为输入源。
	 * <p>
	 * 调用该方法时仅记录路径，不会立即打开文件流；实际加载发生在 {@link #build()}。
	 *
	 * @param path 证书文件路径
	 * @return 当前 Builder
	 */
	public CertificateBuilder path(Path path) {
		this.bytes = null;
		this.inputStream = null;
		this.path = path;
		return this;
	}

	/**
	 * 使用输入流作为输入源。
	 *
	 * @param inputStream 证书输入流
	 * @return 当前 Builder
	 */
	public CertificateBuilder inputStream(InputStream inputStream) {
		this.bytes = null;
		this.inputStream = inputStream;
		this.path = null;
		return this;
	}

	@SneakyThrows
	protected InputStream resolveInputStream() {
		if (Objects.nonNull(bytes)) {
			return new ByteArrayInputStream(bytes);
		}
		if (Objects.nonNull(inputStream)) {
			return inputStream;
		}
		if (Objects.nonNull(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}

	/**
	 * 构建证书数组。
	 * <p>
	 * 按 {@link #bytes}、{@link #inputStream}、{@link #path} 的最后一次有效配置加载证书。
	 *
	 * @return 解析后的证书数组
	 */
	@SneakyThrows
	@Override
	public Certificate[] build() {
		CertificateFactory factory = Objects.nonNull(provider) ? CertificateFactory.getInstance(type, provider) : CertificateFactory.getInstance(type);
		@Cleanup InputStream resolvedInputStream = resolveInputStream();
		Objects.requireNonNull(resolvedInputStream, "inputStream cannot be null");
		Collection<? extends Certificate> certificates = factory.generateCertificates(resolvedInputStream);
		return certificates.toArray(new Certificate[0]);
	}
}
