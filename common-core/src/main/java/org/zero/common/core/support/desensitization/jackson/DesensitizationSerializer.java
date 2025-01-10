package org.zero.common.core.support.desensitization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.zero.common.core.support.desensitization.Desensitization;
import org.zero.common.core.support.desensitization.DesensitizationProvider;
import org.zero.common.core.support.desensitization.DesensitizationType;
import org.zero.common.core.support.desensitization.Desensitizer;
import org.zero.common.data.exception.CommonException;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/12
 */
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizationSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private Desensitization desensitization;
    private DesensitizationProvider desensitizationProvider;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String desensitized = value;
        if (Objects.nonNull(desensitizationProvider)) {
            Desensitizer desensitizer = ClassUtil.createInstance(desensitizationProvider.value(), true);
            if (Objects.isNull(desensitizer)) {
                throw new CommonException("no desensitizer provided");
            }
            desensitized = desensitizer.desensitize(value);
        }
        if (Objects.nonNull(desensitization)) {
            DesensitizationType desensitizationType = desensitization.type();
            if (desensitizationType == DesensitizationType.CUSTOM) {
                desensitized = desensitizationType.desensitize(value, desensitization.start(), desensitization.end(), desensitization.replacement());
            } else {
                desensitized = desensitizationType.desensitize(value);
            }
        }
        gen.writeString(desensitized);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (Objects.nonNull(property)) {
            // 判断数据类型是否为String类型
            if (Objects.equals(property.getType().getRawClass(), String.class)) {
                // 获取自定义注解
                Desensitization desensitization = property.getAnnotation(Desensitization.class);
                DesensitizationProvider desensitizationProvider = property.getAnnotation(DesensitizationProvider.class);
                // 如果字段上没有注解，则从上下文中获取注解
                if (Objects.isNull(desensitization)) {
                    desensitization = property.getContextAnnotation(Desensitization.class);
                }
                if (Objects.isNull(desensitizationProvider)) {
                    desensitizationProvider = property.getContextAnnotation(DesensitizationProvider.class);
                }
                // 如果找到了注解，创建新的序列化实例
                if (Objects.nonNull(desensitization) || Objects.nonNull(desensitizationProvider)) {
                    return new DesensitizationSerializer(desensitization, desensitizationProvider);
                }
            }
            // 默认的序列化处理
            return prov.findValueSerializer(property.getType(), property);
        }
        return prov.getDefaultNullValueSerializer();
    }
}
