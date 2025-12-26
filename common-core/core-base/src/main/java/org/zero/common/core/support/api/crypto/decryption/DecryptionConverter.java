package org.zero.common.core.support.api.crypto.decryption;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.converter.InputConverter;
import org.zero.common.core.support.api.crypto.converter.OutputConverter;
import org.zero.common.core.support.crypto.Decryptor;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.util.SetUtil;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

/**
 * 解密转换器
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addFormatters(org.springframework.format.FormatterRegistry)
 * @since 2025/10/28
 */
public class DecryptionConverter implements ConditionalGenericConverter {
	protected final CryptoProperties.DecryptionProperties decryptionProperties;
	protected final ConversionService conversionService;

	public DecryptionConverter(CryptoProperties.DecryptionProperties decryptionProperties, ConversionService conversionService) {
		this.conversionService = conversionService;
		this.decryptionProperties = decryptionProperties;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return SetUtil.of(new ConvertiblePair(CharSequence.class, Object.class),
			new ConvertiblePair(byte[].class, Object.class));
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return targetType.hasAnnotation(Decryption.class);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (Objects.isNull(source)) {
			return null;
		}

		Decryption decryption = targetType.getAnnotation(Decryption.class);
		if (Objects.isNull(decryption) || !decryption.enable()) {
			if (conversionService.canConvert(sourceType, targetType)) {
				return conversionService.convert(source, sourceType, targetType);
			}
			throw new DecryptionException(String.format("Cannot convert from %s to %s without decryption", sourceType, targetType));
		}

		return this.decryptValue(source, sourceType, targetType, decryption);
	}

	protected Object decryptValue(Object source, TypeDescriptor sourceType,
								  TypeDescriptor targetType, Decryption decryption) {
		CryptoContext context = CryptoUtil.getContext(decryption, decryptionProperties);
		@SuppressWarnings("unchecked")
		InputConverter<Object> sourceConverter = (InputConverter<Object>) context.getSourceConverter();
		byte[] bytes = this.toBytes(source, sourceType, sourceConverter);
		Decryptor decryptor = this.getDecryptor(decryption, decryptionProperties, context);
		byte[] decryptedBytes = this.decrypt(decryptor, bytes);
		@SuppressWarnings("unchecked")
		OutputConverter<Object> targetConverter = (OutputConverter<Object>) context.getTargetConverter();
		return this.toObject(decryptedBytes, targetType, targetConverter);
	}

	protected byte[] toBytes(Object source, TypeDescriptor sourceType, InputConverter<Object> sourceConverter) {
		if (sourceConverter.supports(source)) {
			return sourceConverter.toBytes(source);
		}
		for (Type type : sourceConverter.supportTypes()) {
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(ClassUtil.getRawClass(type));
			if (conversionService.canConvert(sourceType, typeDescriptor)) {
				Object converted = conversionService.convert(source, sourceType, typeDescriptor);
				if (sourceConverter.supports(converted)) {
					return sourceConverter.toBytes(converted);
				}
			}
		}
		throw new DecryptionException(String.format("Cannot convert [%s] of type [%s] to byte array using available converters", source, sourceType));
	}

	protected Object toObject(byte[] target, TypeDescriptor targetType, OutputConverter<Object> targetConverter) {
		Object object = targetConverter.fromBytes(target);
		TypeDescriptor typeDescriptor = TypeDescriptor.forObject(object);
		if (Objects.nonNull(typeDescriptor) && typeDescriptor.isAssignableTo(targetType)) {
			return object;
		}
		if (conversionService.canConvert(typeDescriptor, targetType)) {
			return conversionService.convert(object, typeDescriptor, targetType);
		}
		throw new DecryptionException(String.format("Cannot convert decrypted result [%s] to target type [%s]", typeDescriptor, targetType));
	}

	protected Decryptor getDecryptor(Decryption decryption, CryptoProperties.DecryptionProperties decryptionProperties, CryptoContext context) {
		return CryptoUtil.getInstance(decryption.decryptor(), decryptionProperties.getDecryptor(), context);
	}

	protected byte[] decrypt(Decryptor decryptor, byte[] source) {
		return decryptor.decrypt(source);
	}
}
