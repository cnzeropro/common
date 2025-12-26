package org.zero.common.core.support.api.crypto.decryption;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import lombok.extern.java.Log;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.converter.InputConverter;
import org.zero.common.core.support.api.crypto.converter.OutputConverter;
import org.zero.common.core.support.crypto.Decryptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Jackson 解密反序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
@Log
public class DecryptionDeserializer extends StdDeserializer<Object> implements ContextualDeserializer {
	protected final CryptoProperties.DecryptionProperties decryptionProperties;
	protected final BeanProperty property;

	public DecryptionDeserializer(CryptoProperties.DecryptionProperties decryptionProperties) {
		this(decryptionProperties, null);
	}

	protected DecryptionDeserializer(CryptoProperties.DecryptionProperties decryptionProperties, BeanProperty property) {
		super(Objects.nonNull(property) ? property.getType() : SimpleType.constructUnsafe(Object.class));
		this.decryptionProperties = decryptionProperties;
		this.property = property;
	}

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		Decryption decryption = this.getAnnotation();
		if (Objects.isNull(decryption) || !decryption.enable()) {
			return ctxt.readValue(p, getValueType(ctxt));
		}
		Object rawValue = p.readValueAs(Object.class);
		if (Objects.isNull(rawValue)) {
			return null;
		}
		// 执行解密逻辑
		return this.decryptValue(rawValue, decryption, p, ctxt);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		return new DecryptionDeserializer(decryptionProperties, property);
	}

	protected Decryption getAnnotation() {
		return Objects.isNull(property) ? null : property.getAnnotation(Decryption.class);
	}

	protected Object decryptValue(Object source, Decryption decryption, JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		CryptoContext context = CryptoUtil.getContext(decryption, decryptionProperties);
		@SuppressWarnings("unchecked")
		InputConverter<Object> sourceConverter = (InputConverter<Object>) context.getSourceConverter();
		byte[] bytes = this.toBytes(source, sourceConverter, jsonParser, deserializationContext);
		Decryptor decryptor = this.getDecryptor(decryption, decryptionProperties, context);
		byte[] decryptedBytes = this.decrypt(decryptor, bytes);
		@SuppressWarnings("unchecked")
		OutputConverter<Object> targetConverter = (OutputConverter<Object>) context.getTargetConverter();
		return this.toObject(decryptedBytes, targetConverter, jsonParser, deserializationContext);
	}


	protected byte[] toBytes(Object source, InputConverter<Object> sourceConverter, JsonParser jsonParser, DeserializationContext deserializationContext) {
		if (sourceConverter.supports(source)) {
			return sourceConverter.toBytes(source);
		}
		for (Type type : sourceConverter.supportTypes()) {
			try {
				JavaType javaType = deserializationContext.constructType(type);
				Object object = this.convertValue(source, javaType, jsonParser);
				if (sourceConverter.supports(object)) {
					return sourceConverter.toBytes(object);
				}
			} catch (Exception e) {
				log.log(Level.FINE, String.format("Conversion path failed: [%s] -> [%s] -> byte[]", source.getClass(), type), e);
			}
		}
		throw new DecryptionException("Can't convert " + source + " to byte array");
	}

	protected Object toObject(byte[] target, OutputConverter<Object> targetConverter, JsonParser jsonParser, DeserializationContext deserializationContext) {
		JavaType targetType = this.getValueType(deserializationContext);
		Object object = targetConverter.fromBytes(target);
		if (Objects.isNull(object)) {
			return null;
		}
		if (targetType.isTypeOrSuperTypeOf(object.getClass())) {
			return object;
		}
		return this.convertValue(object, targetType, jsonParser);
	}

	protected Object convertValue(Object value, JavaType javaType, JsonParser jsonParser) {
		ObjectCodec objectCodec = jsonParser.getCodec();
		if (!(objectCodec instanceof ObjectMapper)) {
			throw new DecryptionException("Can't convert value: " + value);
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
				throw new DecryptionException("Can't convert value " + value + " to " + javaType, ex);
			}
		}
	}

	protected Decryptor getDecryptor(Decryption decryption, CryptoProperties.DecryptionProperties decryptionProperties, CryptoContext cryptoContext) {
		return CryptoUtil.getInstance(decryption.decryptor(), decryptionProperties.getDecryptor(), cryptoContext);
	}

	protected byte[] decrypt(Decryptor decryptor, byte[] source) {
		return decryptor.decrypt(source);
	}
}
