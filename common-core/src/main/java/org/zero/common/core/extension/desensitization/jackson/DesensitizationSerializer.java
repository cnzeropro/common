package org.zero.common.core.extension.desensitization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.zero.common.core.extension.desensitization.Desensitization;
import org.zero.common.core.extension.desensitization.DesensitizationProvider;
import org.zero.common.core.extension.desensitization.DesensitizationType;
import org.zero.common.core.util.java.reflect.ReflectUtil;

import java.io.IOException;
import java.lang.reflect.Method;
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
        if (Objects.nonNull(desensitization)) {
            DesensitizationType desensitizationType = desensitization.type();
            if (desensitizationType == DesensitizationType.CUSTOM) {
                desensitized = desensitizationType.desensitize(value, desensitization.start(), desensitization.end(), desensitization.value());
            } else {
                desensitized = desensitizationType.desensitize(value);
            }
        }
        if (Objects.nonNull(desensitizationProvider)) {
            Class<?> type = desensitizationProvider.type();
            String methodName = desensitizationProvider.method();
            Method method = ReflectUtil.getMethodByNameAndParam(type, methodName);
            Object instance = ReflectUtil.newInstance(type);
            desensitized = ReflectUtil.invoke(method, instance, String.class);
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
                if (Objects.nonNull(desensitization)) {
                    desensitization = property.getContextAnnotation(Desensitization.class);
                }
                if (Objects.nonNull(desensitizationProvider)) {
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
