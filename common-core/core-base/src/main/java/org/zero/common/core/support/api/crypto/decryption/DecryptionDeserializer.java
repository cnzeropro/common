package org.zero.common.core.support.api.crypto.decryption;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import lombok.Cleanup;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.crypto.Decryptor;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson 解密反序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
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
		// 处理 null 值
		if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
			return null;
		}
		// 获取字段上的 @Decryption 注解
		Decryption decryption = getAnnotation();
		// 没有注解，使用默认反序列化
		if (Objects.isNull(decryption)) {
			return ctxt.readValue(p, getValueType(ctxt));
		}
		// 解密功能被禁用，使用默认反序列化
		if (!decryption.enable()) {
			return ctxt.readValue(p, getValueType(ctxt));
		}
		// 读取原始值
		Object rawValue = readRawValue(p, ctxt);
		if (Objects.isNull(rawValue)) {
			return null;
		}
		// 执行解密逻辑
		return decryptValue(rawValue, decryption, ctxt);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
		return new DecryptionDeserializer(decryptionProperties, property);
	}

	protected Decryption getAnnotation() {
		return Objects.isNull(property) ? null : property.getAnnotation(Decryption.class);
	}

	protected Object readRawValue(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonToken token = jsonParser.getCurrentToken();
		switch (token) {
			case VALUE_NULL:
				return null;
			case VALUE_STRING:
				return jsonParser.getText();
			case VALUE_EMBEDDED_OBJECT:
				return jsonParser.getEmbeddedObject();
			case VALUE_NUMBER_INT:
			case VALUE_NUMBER_FLOAT:
				return jsonParser.getNumberValue();
			case VALUE_TRUE:
			case VALUE_FALSE:
				return jsonParser.getBooleanValue();
			default:
				return jsonParser.getCurrentValue();
		}
	}

	protected Object decryptValue(Object source, Decryption decryption, DeserializationContext deserializationContext) throws IOException {
		Class<?> sourceClass = source.getClass();
		Class<?> targetClass = getValueType(deserializationContext).getRawClass();

		CryptoContext cryptoContext = CryptoUtil.getContext(decryption, decryptionProperties);
		if (ClassUtil.isAssignable(CharSequence.class, sourceClass)) {
			StringMode sourceStringMode = cryptoContext.getSourceStringMode();
			source = sourceStringMode.toBytes(source.toString());
			sourceClass = byte[].class;
		}

		// 处理字节数组输入
		if (sourceClass == byte[].class) {
			Decryptor decryptor = getDecryptor(decryption, decryptionProperties, cryptoContext);
			byte[] decryptedBytes = this.decrypt(decryptor, (byte[]) source);
			if (targetClass == byte[].class) {
				return decryptedBytes;
			}
			StringMode targetStringMode = cryptoContext.getTargetStringMode();
			String decryptedString = targetStringMode.toString(decryptedBytes);
			if (targetClass == String.class || targetClass == CharSequence.class) {
				return decryptedString;
			}
			return convert(decryptedString, deserializationContext);
		}

		throw new DecryptionException("Unsupported source type for decryption: " + sourceClass);
	}

	protected Decryptor getDecryptor(Decryption decryption, CryptoProperties.DecryptionProperties decryptionProperties, CryptoContext cryptoContext) {
		return CryptoUtil.getInstance(decryption.decryptor(), decryptionProperties.getDecryptor(), cryptoContext);
	}

	protected byte[] decrypt(Decryptor decryptor, byte[] source) {
		return decryptor.decrypt(source);
	}

	protected Object convert(String source, DeserializationContext deserializationContext) throws IOException {
		// 使用 Jackson 的类型转换
		@Cleanup JsonParser newParser = deserializationContext.getParser().getCodec().getFactory().createParser(source);
		// 推进到第一个 token
		newParser.nextToken();
		return deserializationContext.readValue(newParser, getValueType(deserializationContext));
	}
}
