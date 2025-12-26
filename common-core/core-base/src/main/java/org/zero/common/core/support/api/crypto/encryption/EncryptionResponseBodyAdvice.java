package org.zero.common.core.support.api.crypto.encryption;

import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.converter.InputConverter;
import org.zero.common.core.support.api.crypto.converter.OutputConverter;
import org.zero.common.core.support.crypto.Encryptor;

import java.util.Objects;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/10
 */
@Log
@EnableConfigurationProperties(CryptoProperties.class)
@ControllerAdvice
public class EncryptionResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {
	protected final CryptoProperties.EncryptionProperties encryptionProperties;
	@Setter
	protected int order;

	public EncryptionResponseBodyAdvice(CryptoProperties cryptoProperties) {
		this.encryptionProperties = cryptoProperties.getEncryption();
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.hasMethodAnnotation(Encryption.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (Objects.isNull(body)) {
			return null;
		}
		Encryption encryption = returnType.getMethodAnnotation(Encryption.class);
		if (Objects.isNull(encryption) || !encryption.enable()) {
			return body;
		}

		try {
			return this.encryptValue(body, encryption);
		} catch (Exception e) {
			log.log(Level.WARNING, "Encryption failed, returning original body.", e);
			// 加密失败时返回原始响应体，避免破坏整个响应
			return body;
		}
	}

	@Override
	public int getOrder() {
		return order;
	}

	protected Object encryptValue(Object source, Encryption encryption) {
		CryptoContext context = CryptoUtil.getContext(encryption, encryptionProperties);
		@SuppressWarnings("unchecked")
		InputConverter<Object> sourceConverter = (InputConverter<Object>) context.getSourceConverter();
		byte[] bytes = this.toBytes(source, sourceConverter);
		Encryptor encryptor = this.getEncryptor(encryption, encryptionProperties, context);
		byte[] encryptedBytes = this.encrypt(encryptor, bytes);
		@SuppressWarnings("unchecked")
		OutputConverter<Object> targetConverter = (OutputConverter<Object>) context.getTargetConverter();
		return this.toObject(encryptedBytes, targetConverter);
	}

	protected byte[] toBytes(Object source, InputConverter<Object> sourceConverter) {
		if (sourceConverter.supports(source)) {
			return sourceConverter.toBytes(source);
		}
		throw new EncryptionException("Can't convert [" + source + "] to byte array");
	}

	protected Object toObject(byte[] source, OutputConverter<Object> targetConverter) {
		return targetConverter.fromBytes(source);
	}

	protected Encryptor getEncryptor(Encryption encryption, CryptoProperties.EncryptionProperties encryptionProperties, CryptoContext cryptoContext) {
		return CryptoUtil.getInstance(encryption.encryptor(), encryptionProperties.getEncryptor(), cryptoContext);
	}

	protected byte[] encrypt(Encryptor encryptor, byte[] source) {
		return encryptor.encrypt(source);
	}
}
