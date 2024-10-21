package org.zero.common.core.support.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2021/8/12
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGVARCHAR})
public class JacksonTypeHandler extends BaseJsonTypeHandler<Object> {
    private final Class<?> propertyType;

    private static ObjectMapper objectMapper;

    public JacksonTypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    protected Object parseJson(String json) {
        try {
            return getObjectMapper().readValue(json, propertyType);
        } catch (IOException e) {
            log.warn(String.format("Can not parse json string: %s", json), e);
            return null;
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn(String.format("Can not convert object to Json: %s", object), e);
            return null;
        }
    }

    protected static ObjectMapper getObjectMapper() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JacksonTypeHandler.objectMapper = objectMapper;
    }
}
