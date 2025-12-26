package org.zero.common.core.support.api.crypto.encryption;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import lombok.extern.java.Log;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.converter.InputConverter;
import org.zero.common.core.support.api.crypto.converter.OutputConverter;
import org.zero.common.core.support.crypto.Encryptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Jackson 加密序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
@Log
public class EncryptionSerializer extends StdSerializer<Object> implements ContextualSerializer {
	protected final CryptoProperties.EncryptionProperties encryptionProperties;
	protected final BeanProperty property;

	public EncryptionSerializer(CryptoProperties.EncryptionProperties encryptionProperties) {
		this(encryptionProperties, null);
	}

	protected EncryptionSerializer(CryptoProperties.EncryptionProperties encryptionProperties, BeanProperty property) {
		super(Objects.nonNull(property) ? property.getType() : SimpleType.constructUnsafe(Object.class));
		this.encryptionProperties = encryptionProperties;
		this.property = property;
	}

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		Encryption encryption = this.getAnnotation();
		if (Objects.isNull(value) || Objects.isNull(encryption) || !encryption.enable()) {
			// 考虑到可能需要序列化配置，因此不使用 gen.writeObject(value);
			provider.defaultSerializeValue(value, gen);
			return;
		}

		Object encryptedValue = this.encryptValue(value, encryption, gen, provider);
		gen.writeObject(encryptedValue);
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		return new EncryptionSerializer(encryptionProperties, property);
	}

	protected Encryption getAnnotation() {
		return Objects.isNull(property) ? null : property.getAnnotation(Encryption.class);
	}

	protected Object encryptValue(Object source, Encryption encryption, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
		CryptoContext context = CryptoUtil.getContext(encryption, encryptionProperties);
		@SuppressWarnings("unchecked")
		InputConverter<Object> sourceConverter = (InputConverter<Object>) context.getSourceConverter();
		byte[] bytes = this.toBytes(source, sourceConverter, jsonGenerator, serializerProvider);
		Encryptor encryptor = this.getEncryptor(encryption, encryptionProperties, context);
		byte[] encryptedBytes = this.encrypt(encryptor, bytes);
		@SuppressWarnings("unchecked")
		OutputConverter<Object> targetConverter = (OutputConverter<Object>) context.getTargetConverter();
		return this.toObject(encryptedBytes, targetConverter, jsonGenerator, serializerProvider);
	}

	protected byte[] toBytes(Object source, InputConverter<Object> sourceConverter, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
		if (sourceConverter.supports(source)) {
			return sourceConverter.toBytes(source);
		}
		for (Type type : sourceConverter.supportTypes()) {
			try {
				JavaType javaType = serializerProvider.constructType(type);
				Object convertedValue = this.convertValue(source, javaType, jsonGenerator);
				if (sourceConverter.supports(convertedValue)) {
					return sourceConverter.toBytes(convertedValue);
				}
			} catch (Exception e) {
				log.log(Level.FINE, String.format("Conversion path failed: [%s] -> [%s] -> byte[]", source.getClass(), type), e);
			}
		}
		throw new EncryptionException("Can't convert [" + source + "] to byte array");
	}

	protected Object toObject(byte[] source, OutputConverter<Object> targetConverter, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
		return targetConverter.fromBytes(source);
	}

	protected Object convertValue(Object value, JavaType javaType, JsonGenerator jsonGenerator) {
		ObjectCodec objectCodec = jsonGenerator.getCodec();
		if (!(objectCodec instanceof ObjectMapper)) {
			throw new EncryptionException("Can't convert value: " + value);
		}
		ObjectMapper objectMapper = (ObjectMapper) objectCodec;
		try {
			return objectMapper.convertValue(value, javaType);
		} catch (Exception e) {
			log.log(Level.FINE, "Direct value conversion failed, attempting JSON serialization/deserialization fallback", e);
			try {
				String json = objectMapper.writeValueAsString(value);
				return objectMapper.readValue(json, javaType);
			} catch (Exception ex) {
				throw new EncryptionException("Can't convert value " + value + " to " + javaType, ex);
			}
		}
	}

	protected Encryptor getEncryptor(Encryption encryption, CryptoProperties.EncryptionProperties encryptionProperties, CryptoContext cryptoContext) {
		return CryptoUtil.getInstance(encryption.encryptor(), encryptionProperties.getEncryptor(), cryptoContext);
	}

	protected byte[] encrypt(Encryptor encryptor, byte[] source) {
		return encryptor.encrypt(source);
	}
}
