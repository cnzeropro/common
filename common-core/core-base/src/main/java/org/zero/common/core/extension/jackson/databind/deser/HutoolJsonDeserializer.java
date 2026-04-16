package org.zero.common.core.extension.jackson.databind.deser;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
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
		JsonToken token = p.currentToken();
		if (token == null) {
			token = p.nextToken();
		}
		if (token == null || JsonToken.VALUE_NULL.equals(token)) {
			return null;
		}

		if (JsonToken.START_OBJECT.equals(token) || JsonToken.START_ARRAY.equals(token)) {
			JsonNode jsonNode = p.readValueAsTree();
			return JSONUtil.parse(jsonNode.toString());
		}

		if (JsonToken.VALUE_STRING.equals(token)) {
			String text = p.getText();
			if (isJsonText(text)) {
				return JSONUtil.parse(text);
			}
			return ctxt.reportInputMismatch(handledType(), "String value is not valid JSON text for Hutool JSON");
		}

		return ctxt.reportInputMismatch(handledType(), "Token %s cannot be deserialized as Hutool JSON", token);
	}

	private boolean isJsonText(String text) {
		if (text == null) {
			return false;
		}
		String trimmed = text.trim();
		if (trimmed.isEmpty()) {
			return false;
		}
		char first = trimmed.charAt(0);
		return first == '{' || first == '[';
	}
}
