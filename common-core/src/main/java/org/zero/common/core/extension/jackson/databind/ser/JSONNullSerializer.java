package org.zero.common.core.extension.jackson.databind.ser;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/22
 */
public  class JSONNullSerializer extends StdSerializer<JSONNull> {
    protected JSONNullSerializer() {
        super(JSONNull.class);
    }

    @Override
    public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNull();
    }
}
