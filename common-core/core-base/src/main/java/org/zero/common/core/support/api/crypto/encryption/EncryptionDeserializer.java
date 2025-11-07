package org.zero.common.core.support.api.crypto.encryption;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoProperties;
import org.zero.common.core.support.api.crypto.CryptoUtil;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.crypto.Encryptor;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson 加密序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
public class EncryptionDeserializer extends StdSerializer<Object> implements ContextualSerializer {
	protected final CryptoProperties.EncryptionProperties encryptionProperties;
	protected final BeanProperty property;

	public EncryptionDeserializer(CryptoProperties.EncryptionProperties encryptionProperties) {
		this(encryptionProperties, null);
	}

	protected EncryptionDeserializer(CryptoProperties.EncryptionProperties encryptionProperties, BeanProperty property) {
		super(Objects.nonNull(property) ? property.getType() : SimpleType.constructUnsafe(Object.class));
		this.encryptionProperties = encryptionProperties;
		this.property = property;
	}

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		// 处理 null 值
		if (Objects.isNull(value)) {
			gen.writeNull();
			return;
		}
		// 获取字段上的 @Encryption 注解
		Encryption encryption = getAnnotation();
		// 没有注解，使用默认反序列化
		if (Objects.isNull(encryption)) {
			gen.writeObject(value);
			return;
		}
		// 加密功能被禁用，使用默认反序列化
		if (!encryption.enable()) {
			gen.writeObject(value);
			return;
		}
		// 执行加密逻辑
		Object encryptedValue = encryptValue(value, encryption, provider);
		gen.writeObject(encryptedValue);
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		return new EncryptionDeserializer(encryptionProperties, property);
	}

	protected Encryption getAnnotation() {
		return Objects.isNull(property) ? null : property.getAnnotation(Encryption.class);
	}

	protected Object encryptValue(Object source, Encryption encryption, SerializerProvider serializerProvider) throws IOException {
		Class<?> sourceClass = source.getClass();
		Class<?> targetClass = this.handledType();

		CryptoContext cryptoContext = CryptoUtil.getContext(encryption, encryptionProperties);
		if (ClassUtil.isAssignable(CharSequence.class, sourceClass)) {
			StringMode sourceStringMode = cryptoContext.getSourceStringMode();
			source = sourceStringMode.toBytes(source.toString());
			sourceClass = byte[].class;
		}

		// 处理字节数组输入
		if (sourceClass == byte[].class) {
			Encryptor encryptor = getEncryptor(encryption, encryptionProperties, cryptoContext);
			byte[] encryptedBytes = this.encrypt(encryptor, (byte[]) source);
			if (targetClass == byte[].class) {
				return encryptedBytes;
			}
			StringMode targetStringMode = cryptoContext.getTargetStringMode();
			String encryptedString = targetStringMode.toString(encryptedBytes);
			if (targetClass == String.class || targetClass == CharSequence.class) {
				return encryptedString;
			}
		}

		throw new EncryptionException("Unsupported source type for encryption: " + sourceClass);
	}

	protected Encryptor getEncryptor(Encryption encryption, CryptoProperties.EncryptionProperties encryptionProperties, CryptoContext cryptoContext) {
		return CryptoUtil.getInstance(encryption.encryptor(), encryptionProperties.getEncryptor(), cryptoContext);
	}

	protected byte[] encrypt(Encryptor encryptor, byte[] source) {
		return encryptor.encrypt(source);
	}
}
