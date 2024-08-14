package org.zero.common.core.util.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.zero.common.core.util.spring.context.SpringContextUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * @author zero
 * @since 2023/7/19
 */
@UtilityClass
public class JacksonUtils {
    private static volatile ObjectMapper objectMapper;

    @SneakyThrows
    public static String toJsonStr(Object value) {
        return getObjectMapper().writeValueAsString(value);
    }

    public static <T> T toObj(String jsonStr, Type type) {
        JavaType javaType = getObjectMapper().constructType(type);
        return toObj(jsonStr, javaType);
    }

    @SneakyThrows
    public static <T> T toObj(String jsonStr, Class<T> clazz) {
        return getObjectMapper().readValue(jsonStr, clazz);
    }

    @SneakyThrows
    public static <T> T toObj(String jsonStr, TypeReference<T> typeReference) {
        return getObjectMapper().readValue(jsonStr, typeReference);
    }

    @SneakyThrows
    public static <T> T toObj(String jsonStr, JavaType javaType) {
        return getObjectMapper().readValue(jsonStr, javaType);
    }

    public static <T> Collection<T> toCollection(String jsonStr, Class<? extends Collection<?>> collectionClass, Class<?> elementClass) {
        CollectionType collectionType = getObjectMapper().getTypeFactory().constructCollectionType(collectionClass, elementClass);
        return toObj(jsonStr, collectionType);
    }

    public static <T> T[] toArray(String jsonStr, Class<?> elementClass) {
        ArrayType arrayType = getObjectMapper().getTypeFactory().constructArrayType(elementClass);
        return toObj(jsonStr, arrayType);
    }

    private static ObjectMapper getObjectMapper() {
        if (Objects.isNull(objectMapper)) {
            synchronized (JacksonUtils.class) {
                if (Objects.isNull(objectMapper)) {
                    objectMapper = SpringContextUtils.getBean(ObjectMapper.class);
                }
            }
        }
        return objectMapper;
    }
}
