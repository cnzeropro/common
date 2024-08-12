package org.zero.common.core.extend.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/12
 */
@Slf4j
public class JacksonTypeHandler extends BaseJsonTypeHandler<Object> {
    private static ObjectMapper objectMapper;
    private final Class<?> propertyType;
    private Class<?> genericType;
    private JavaType javaType;

    public JacksonTypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public JacksonTypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
    }

    @Override
    protected Object parseJson(String json) {
        try {
            if (Objects.nonNull(genericType) && Collection.class.isAssignableFrom(propertyType)) {
                return getObjectMapper().readValue(json, this.getJavaType());
            } else {
                return getObjectMapper().readValue(json, propertyType);
            }
        } catch (IOException e) {
            log.warn(String.format("Can not parse json by JacksonTypeHandler: %s", json), e);
            return null;
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn(String.format("Can not convert object to Json by JacksonTypeHandler: %s", object), e);
            return null;
        }
    }


    public JavaType getJavaType() {
        if (Objects.isNull(javaType)) {
            javaType = getObjectMapper().getTypeFactory()
                    .constructCollectionType((Class<? extends Collection>) propertyType, genericType);
        }
        return javaType;
    }

    protected static ObjectMapper getObjectMapper() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    protected static void setObjectMapper(ObjectMapper objectMapper) {
        JacksonTypeHandler.objectMapper = objectMapper;
    }
}
