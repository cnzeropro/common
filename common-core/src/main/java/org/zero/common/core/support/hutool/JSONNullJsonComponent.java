package org.zero.common.core.support.hutool;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
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
