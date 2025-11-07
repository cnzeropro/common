package org.zero.common.core.support.api.crypto.decryption;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.zero.common.core.extension.spring.core.convert.AnnotationConverter;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.crypto.Decryptor;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.util.SetUtil;

import java.util.Objects;
import java.util.Set;

/**
 * 解密转换器
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addFormatters(org.springframework.format.FormatterRegistry)
 * @since 2025/10/28
 */
public class DecryptionConverter extends AnnotationConverter<Decryption> {
	protected final CryptoProperties.DecryptionProperties decryptionProperties;
	protected final ConversionService conversionService;

	public DecryptionConverter(CryptoProperties.DecryptionProperties decryptionProperties, ConversionService conversionService) {
		super(Decryption.class);
		this.conversionService = conversionService;
		this.decryptionProperties = decryptionProperties;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return SetUtil.of(new ConvertiblePair(CharSequence.class, Object.class),
			new ConvertiblePair(byte[].class, Object.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (Objects.isNull(source)) {
			return null;
		}

		Class<?> sourceClass = sourceType.getType();
		Class<?> targetClass = targetType.getType();
		Decryption decryption = this.getAnnotation(sourceType, targetType);
		if (Objects.isNull(decryption) || !decryption.enable()) {
			if (conversionService.canConvert(sourceType, targetType)) {
				return conversionService.convert(source, sourceType, targetType);
			}
			throw new DecryptionException("Cannot convert from " + sourceType + " to " + targetType);
		}

		CryptoContext context = CryptoUtil.getContext(decryption, decryptionProperties);
		if (ClassUtil.isAssignable(CharSequence.class, sourceClass)) {
			StringMode sourceStringMode = context.getSourceStringMode();
			source = sourceStringMode.toBytes(source.toString());
			sourceClass = byte[].class;
		}
		if (sourceClass == byte[].class) {
			Decryptor decryptor = this.getDecryptor(decryption, decryptionProperties, context);
			byte[] bytes = this.decrypt(decryptor, (byte[]) source);
			if (targetClass == byte[].class) {
				return bytes;
			}
			StringMode targetStringMode = context.getTargetStringMode();
			String string = targetStringMode.toString(bytes);
			if (targetClass == String.class || targetClass == CharSequence.class) {
				return string;
			}
			if (conversionService.canConvert(TypeDescriptor.valueOf(String.class), targetType)) {
				return conversionService.convert(string, TypeDescriptor.valueOf(String.class), targetType);
			}
		}
		throw new DecryptionException("Can't convert [" + source + "] to " + targetType);
	}

	protected Decryptor getDecryptor(Decryption decryption, CryptoProperties.DecryptionProperties decryptionProperties, CryptoContext context) {
		return CryptoUtil.getInstance(decryption.decryptor(), decryptionProperties.getDecryptor(), context);
	}

	protected byte[] decrypt(Decryptor decryptor, byte[] source) {
		return decryptor.decrypt(source);
	}
}
