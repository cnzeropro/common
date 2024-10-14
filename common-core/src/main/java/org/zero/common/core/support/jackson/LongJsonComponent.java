package org.zero.common.core.support.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Objects;

/**
 * 注册 {@link Long} 类型的 Jackson 序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/9
 */
@JsonComponent
public class LongJsonComponent {
    public static class LongSerializer extends StdSerializer<Long> {
        protected LongSerializer(Class<Long> t) {
            super(t);
        }

        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (Objects.isNull(value)) {
                gen.writeNull();
            } else {
                gen.writeNumber(value);
            }
        }
    }

    public static class LongDeserializer extends StdDeserializer<Long> {
        protected LongDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return p.getValueAsLong();
        }
    }
}
