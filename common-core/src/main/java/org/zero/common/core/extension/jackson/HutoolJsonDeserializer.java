package org.zero.common.core.extension.jackson;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/31
 */
public class HutoolJsonDeserializer extends StdDeserializer<JSON> {
    public HutoolJsonDeserializer() {
        super(JSON.class);
    }

    @Override
    public JSON deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String text = p.getText();
        return JSONUtil.parse(text);
    }
}
