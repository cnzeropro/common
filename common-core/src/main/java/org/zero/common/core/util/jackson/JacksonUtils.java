package org.zero.common.core.util.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.util.spring.SpringUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * @author zero
 * @since 2023/7/19
 */
// @UtilityClass
public class JacksonUtils {
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

    /* **************************************************** Other **************************************************** */
    @Setter
    private static volatile ObjectMapper objectMapper;

    /**
     * 获取工具类中 {@link ObjectMapper} 实例
     * <p>
     * 注意：请勿修改获取到的 {@link ObjectMapper} 实例中的属性或者使其为 null，否则可能会影响工具类的使用。
     * 如要修改，请使用 {@link #copyObjectMapper()} 方法拷贝  {@link ObjectMapper} 实例。
     */
    public static ObjectMapper getObjectMapper() {
        checkAndCreate();
        return objectMapper;
    }

    public static ObjectMapper copyObjectMapper() {
        return getObjectMapper().copy();
    }

    protected static void checkAndCreate() {
        if (Objects.isNull(objectMapper)) {
            synchronized (JacksonUtils.class) {
                if (Objects.isNull(objectMapper)) {
                    objectMapper = SpringUtils.getBeanProvider(ObjectMapper.class).getIfAvailable();
                }
                if (Objects.isNull(objectMapper)) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
    }

    protected JacksonUtils() {
        throw new UnsupportedOperationException();
    }
}
