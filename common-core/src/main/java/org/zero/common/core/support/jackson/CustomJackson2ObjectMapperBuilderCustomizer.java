package org.zero.common.core.support.jackson;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/16
 */
public class CustomJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        // long primitive
        jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        // Long wrapped
        jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
        // BigInteger
        jacksonObjectMapperBuilder.serializerByType(BigInteger.class, ToStringSerializer.instance);
        // BigDecimal
        jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
    }
}
