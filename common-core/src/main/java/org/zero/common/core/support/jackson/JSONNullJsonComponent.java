package org.zero.common.core.support.jackson;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 注册 Hutool {@link JSONNull} 类型的 Jackson 序列化器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/9
 */
@JsonComponent
public class JSONNullJsonComponent {
    public static class JSONNullSerializer extends StdSerializer<JSONNull> {
        protected JSONNullSerializer(Class<JSONNull> t) {
            super(t);
        }

        @Override
        public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNull();
        }
    }
}
